package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.domain.*
import com.urosjarc.slotraffic.exceptions.AuthException
import com.urosjarc.slotraffic.res.AuthRes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.File


class SloTraffic(
    val username: String,
    val password: String
) {

    private lateinit var authRes: AuthRes

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 100000
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    init {
        runBlocking { authRes = login() }
    }

    @OptIn(InternalAPI::class)
    suspend fun login(): AuthRes {
        val res = client.post(url("/uc/user/token")) {
            this.body = FormDataContent(Parameters.build {
                append("grant_type", "password")
                append("username", username)
                append("password", password)
            })
        }

        if (res.status.value !in 200..299)
            throw AuthException("Could not authenticate user: $res")

        return res.body<AuthRes>()
    }

    private fun url(path: String): String {
        return "https://b2b.nap.si${path}"
    }

    private suspend fun getData(name: String): HttpResponse {
        return client.get(url("/data/$name")) {
            header("Authorization", "Bearer ${authRes.access_token}")
        }
    }

    suspend fun getDataToFile(data: String) {
        val res = getData(data)
        File("$data.xml").writeText(res.bodyAsText())
    }

    suspend fun getCameras(): List<Camera> {
        val res = getData("b2b.cameras.datexii33")
        val inputStream = res.bodyAsChannel().toInputStream()
        val doc: Document = Jsoup.parse(inputStream, null, "", Parser.xmlParser())
        val cameras = mutableListOf<Camera>()
        for (plr in doc.select("predefinedLocationReference")) {
            val lat = plr.selectFirst("latitude")!!.text().toDouble()
            val lon = plr.selectFirst("longitude")!!.text().toDouble()
            val img = plr.selectFirst("stillImageUrl")!!.text()

            //Title
            val lang_to_titles = mutableMapOf<String, String>()
            for (value in plr.selectFirst("cameraTitle")!!.select("value")) {
                lang_to_titles[value.attr("lang")] = value.text()
            }

            //Description
            val lang_to_description = mutableMapOf<String, String>()
            for (value in plr.selectFirst("cameraDescription")!!.select("value")) {
                lang_to_description[value.attr("lang")] = value.text()
            }

            //Group
            val lang_to_group = mutableMapOf<String, String>()
            for (value in plr.selectFirst("groupName")!!.select("value")) {
                lang_to_group[value.attr("lang")] = value.text()
            }

            //Region
            val lang_to_region = mutableMapOf<String, String>()
            for (value in plr.selectFirst("regionName")!!.select("value")) {
                lang_to_region[value.attr("lang")] = value.text()
            }

            cameras.add(
                Camera(
                    location = Location(lat = lat, lon = lon),
                    imgUrl = img,
                    title = lang_to_titles,
                    desciption = lang_to_description,
                    group = lang_to_group,
                    region = lang_to_region
                )
            )

        }

        return cameras
    }

    suspend fun getCounters(): List<Counter> {

        val res = getData("b2b.counters.datexii33")
        val inputStream = res.bodyAsChannel().toInputStream()
        val doc: Document = Jsoup.parse(inputStream, null, "", Parser.xmlParser())
        val counters = mutableListOf<Counter>()

        for (siteMeasurement in doc.select("siteMeasurements")) {
            val index = siteMeasurement.selectFirst("pertinentLocation")!!.attr("xsi:type").split(":").first()
            val lat = siteMeasurement.selectFirst("$index|latitude")!!.text().toDouble()
            val lon = siteMeasurement.selectFirst("$index|longitude")!!.text().toDouble()
            val trafficFlow = doc.selectFirst("basicData[xsi:type=TrafficFlow]")!!
            val trafficSpeed = doc.selectFirst("basicData[xsi:type=TrafficSpeed]")!!
            val trafficHeadway = doc.selectFirst("basicData[xsi:type=TrafficHeadway]")!!
            val trafficConcentration = doc.selectFirst("basicData[xsi:type=TrafficConcentration]")!!

            counters.add(
                Counter(
                    location = Location(lat = lat, lon = lon),
                    trafficSpeed = TraficSpeed(
                        instant = trafficSpeed.selectFirst("timeValue")!!.text(),
                        averageSpeed = trafficSpeed.selectFirst("speed")!!.text().toFloat()
                    ),
                    trafficFlow = TraficFlow(
                        startInstant = trafficFlow.selectFirst("startOfPeriod")!!.text(),
                        endInstant = trafficFlow.selectFirst("endOfPeriod")!!.text(),
                        flowRate = trafficFlow.selectFirst("vehicleFlowRate")!!.text().toInt()
                    ),
                    trafficConcentration = TrafficConcentration(
                        instant = trafficConcentration.selectFirst("timeValue")!!.text(),
                        density = trafficConcentration.selectFirst("densityOfVehicles")!!.text().toInt()
                    ),
                    trafficHeadway = TrafficHeadway(
                        instant = trafficConcentration.selectFirst("timeValue")!!.text(),
                        averageDistance = trafficHeadway.selectFirst("averageDistanceHeadway")!!.text().toDouble(),
                        averageTime = trafficHeadway.selectFirst("averageTimeHeadway")!!.text().toFloat()
                    ),
                )
            )
        }

        return counters
    }

    suspend fun getEvents(): List<Event> {
        val res = getData("b2b.events.datexii33")
        val inputStream = res.bodyAsChannel().toInputStream()
        val doc: Document = Jsoup.parse(inputStream, null, "", Parser.xmlParser())
        val events = mutableListOf<Event>()

        for (sit in doc.select("situation")) {
            val commentEle = sit.selectFirst("generalPublicComment")!!
            val locRef = sit.selectFirst("locationReference")!!
            val index = locRef.attr("xsi:type").split(":").first()

            val lat = locRef.selectFirst("$index|latitude")!!.text().toDouble()
            val lon = locRef.selectFirst("$index|longitude")!!.text().toDouble()
            val probability = sit.selectFirst("probabilityOfOccurrence")!!.text()
            val severity = sit.selectFirst("severity")!!.text()
            val startTime = sit.selectFirst("overallStartTime")!!.text()
            val endTime = sit.selectFirst("overallEndTime")!!.text()
            val comment = commentEle.select("value").associate { it.attribute("lang").value to it.text() }

            events.add(
                Event(
                    lat = lat,
                    lon = lon,
                    probability = probability,
                    severity = severity,
                    startTime = startTime,
                    endTime = endTime,
                    comment = comment
                )
            )
        }

        return events
    }
}
