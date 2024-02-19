package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.domain.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import java.util.*
import kotlin.concurrent.schedule

class MapVisualizer {
    companion object {

        lateinit var client: SloTraffic

        var cameras = listOf<Camera>()
        var weather = Weather()
        var counters = listOf<Counter>()
        var events = listOf<Event>()
        var restAreas = listOf<RestArea>()
        var roadWorks = listOf<Event>()

        fun updateCache() {
            runBlocking {
                try { cameras = client.getCameras() } catch (e: Throwable) { throw e }
                try { weather = client.getWeather()  } catch (e: Throwable) { throw e }
                try { counters = client.getCounters() } catch (e: Throwable) { throw e }
                try { events = client.getEvents() } catch (e: Throwable) { throw e }
                try { restAreas = client.getRestAreas() } catch (e: Throwable) { throw e }
                try { roadWorks = client.getRoadWorks() } catch (e: Throwable) { throw e }
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {

            client = SloTraffic(
                username = Env.USERNAME,
                password = Env.PASSWORD
            )

            updateCache()

            Timer().schedule(1000L * 60 * 60) { updateCache() }

            val port = System.getenv("PORT")?.toInt() ?: 8080

            embeddedServer(Netty, port = port, host = "0.0.0.0") {
                install(ContentNegotiation) {
                    json(Json {
                        allowSpecialFloatingPointValues = true
                        prettyPrint = true
                        isLenient = true
                    })
                }
                install(CORS) {
                    this.allowHeader(HttpHeaders.ContentType)
                    this.allowHeader(HttpHeaders.Authorization)
                    this.anyHost()
                    this.allowMethod(HttpMethod.Post)
                    this.allowMethod(HttpMethod.Put)
                    this.allowMethod(HttpMethod.Get)
                    this.allowMethod(HttpMethod.Delete)
                }
                this.install(CallLogging) {
                    this.level = Level.DEBUG
                    format { call ->
                        val status = call.response.status()
                        val httpMethod = call.request.httpMethod.value
                        val userAgent = call.request.headers["User-Agent"]
                        "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
                    }
                }

                routing {
                    staticResources("/", "app") { default("index.html") }
                    get("/cameras") { call.respond(cameras) }
                    get("/weather") { call.respond(weather) }
                    get("/counters") { call.respond(counters) }
                    get("/events") { call.respond(events) }
                    get("/rest-areas") { call.respond(restAreas) }
                    get("/road-work") { call.respond(roadWorks) }

                }
            }.start(wait = true)
        }
    }
}
