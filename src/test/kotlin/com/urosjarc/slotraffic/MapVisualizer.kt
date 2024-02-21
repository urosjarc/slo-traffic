package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.geojson.*
import com.urosjarc.slotraffic.netex.Fare
import com.urosjarc.slotraffic.netex.Operator
import com.urosjarc.slotraffic.netex.StopPlace
import com.urosjarc.slotraffic.netex.Timetable
import com.urosjarc.slotraffic.res.GeoJson
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
import org.apache.logging.log4j.kotlin.logger
import org.slf4j.event.Level
import java.util.*
import kotlin.concurrent.schedule

class MapVisualizer {
    companion object {

        val log = logger()
        lateinit var client: SloTraffic

        lateinit var cameras: GeoJson<CameraProps, Unit>
        lateinit var roadworks: GeoJson<RoadworkProps, RoadworkMeta>
        lateinit var restAreas: GeoJson<RestAreaProps, Unit>
        lateinit var events: GeoJson<EventProps, EventMeta>
        lateinit var counters: GeoJson<CounterProps, CounterMeta>
        lateinit var winds: GeoJson<WindProps, WindMeta>
        lateinit var borderDelays: GeoJson<BorderDelayProps, BorderDelayMeta>
        lateinit var stopPlaces: Map<String, StopPlace>
        lateinit var operators: Map<String, Operator>
        lateinit var fares: Map<String, Fare>
        lateinit var timetables: Map<String, Timetable>

        fun tryExecute(cb: () -> Unit) {
            try {
                cb()
            } catch (e: Throwable) {
                log.fatal(e)
            }
        }

        fun updateCache() {
            tryExecute { runBlocking { cameras = client.getCameras() } }
            tryExecute { runBlocking { roadworks = client.getRoadworks() } }
            tryExecute { runBlocking { restAreas = client.getRestAreas() } }
            tryExecute { runBlocking { events = client.getEvents() } }
            tryExecute { runBlocking { counters = client.getCounters() } }
            tryExecute { runBlocking { winds = client.getWinds() } }
            tryExecute { runBlocking { borderDelays = client.getBorderDelays() } }
            tryExecute { runBlocking { stopPlaces = client.getStopPlaces() } }
            tryExecute { runBlocking { operators = client.getOperators() } }
            tryExecute { runBlocking { fares = client.getFares() } }
            tryExecute { runBlocking { timetables = client.getTimetables() } }
        }

        @JvmStatic
        fun main(args: Array<String>) {

            client = SloTraffic(
                username = Env.USERNAME,
                password = Env.PASSWORD
            )

            updateCache()
            Timer().schedule(1000L * 60 * 15) { updateCache() }

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
                    get("/counters") { call.respond(counters) }
                    get("/events") { call.respond(events) }
                    get("/rest-areas") { call.respond(restAreas) }
                    get("/road-work") { call.respond(roadworks) }
                    get("/winds") { call.respond(winds) }
                    get("/border-delays") { call.respond(borderDelays) }
                    get("/stop-places") { call.respond(stopPlaces) }
                    get("/operators") { call.respond(operators.values) }
                    get("/fares") { call.respond(fares.values) }
                    get("/timetables") { call.respond(timetables.values.toList().subList(0,300)) }
                }
            }.start(wait = true)
        }
    }
}
