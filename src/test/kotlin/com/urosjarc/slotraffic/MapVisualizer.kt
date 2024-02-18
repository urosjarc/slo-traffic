package com.urosjarc.slotraffic

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

class MapVisualizer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val client = SloTraffic(
                username = Env.USERNAME,
                password = Env.PASSWORD
            )

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
                    get("/cameras") { withContext(Dispatchers.IO) { call.respond(client.getCameras()) } }
                    get("/weather") { withContext(Dispatchers.IO) { call.respond(client.getWeather()) } }
                    get("/counters") { withContext(Dispatchers.IO) { call.respond(client.getCounters()) } }
                    get("/events") { withContext(Dispatchers.IO) { call.respond(client.getEvents()) } }
                    get("/rest-areas") { withContext(Dispatchers.IO) { call.respond(client.getRestAreas()) } }
                    get("/road-work") { withContext(Dispatchers.IO) { call.respond(client.getRoadWorks()) } }
                }
            }.start(wait = true)
        }
    }
}
