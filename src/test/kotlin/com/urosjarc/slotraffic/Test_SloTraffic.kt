package com.urosjarc.slotraffic

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test

class Test_SloTraffic {

    companion object {
        private lateinit var client: SloTraffic

        @JvmStatic
        @BeforeAll
        fun init() {
            this.client = SloTraffic(username = Env.USERNAME, password = Env.PASSWORD)
        }
    }

    @Test
    fun `test get cameras`(): Unit = runBlocking {
        client.getCameras().forEach {
            println(it)
        }
    }

    @Test
    fun `test get counters`(): Unit = runBlocking {
        client.getCounters().forEach {
            println(it)
        }
    }

    @Test
    fun `test get events`(): Unit = runBlocking {
        client.getEvents().forEach {
            println(it)
        }
    }

    @Test
    fun `test get data`(): Unit = runBlocking {
        client.getDataToFile(data="b2b.events.datexii33")
    }

    @Test
    fun main(): Unit = runBlocking {
        listOf(
            "b2b.alertc-ltef",
            "b2b.cameras.georss",
            "b2b.cameras.datexii33",
            "b2b.cameras.geojson",
            "b2b.cameras",
            "b2b.weather.dars",
            "b2b.weather.dars.datexii33",
            "b2b.tpeg.wea.dars",
            "b2b.roadworks",
            "b2b.roadworks.geojson",
            "b2b.roadworks.georss",
            "b2b.roadworks.json",
            "b2b.roadworks.rss",
            "b2b.roadworks.datexii33",
            "b2b.roadworks.rdstmc",
            "b2b.roadworks.tpeg",
            "b2b.weather.drsi",
            "b2b.weather.drsi1.datexii33",
            "b2b.tpeg.wea.drsi",
            "b2b.weather.drsi2.datexii33",
            "b2b.fcd.tpeg.tfp",
            "b2b.fcd.datexii33",
            "b2b.netex",
            "b2b.truckparking",
            "b2b.restareas.datexii33",
            "b2b.restareas.geojson",
            "b2b.restareas.json",
            "b2b.traveltimes.promet.datexii33",
            "b2b.tpeg.emi",
            "b2b.prometej.energyInfrastructureStatusPublication",
            "b2b.prometej.energyInfrastructureTablePublication",
            "b2b.events",
            "b2b.srti.datexii33",
            "b2b.events.geojson",
            "b2b.events.georss",
            "b2b.events.json",
            "b2b.events.rss",
            "b2b.events.datexii33",
            "b2b.events.rdstmc",
            "b2b.events.tpeg",
            "b2b.traffic-forecast.rss",
            "b2b.traffic-report.json",
            "b2b.traffic-report.rss",
            "b2b.siri.api",
            "b2b.dars.vms.datexii23.status",
            "b2b.dars.vms.datexii3.status",
            "b2b.dars.vms.datexii23.table",
            "b2b.dars.vms.datexii3.table",
            "b2b.counters",
            "b2b.counters.datexii33",
            "b2b.counters.geojson",
            "b2b.counters.georss",
            "b2b.tn-its",
            "b2b.ujma23.geojson",
            "b2b.wind",
            "b2b.wind.datexii33",
            "b2b.wind.geojson",
            "b2b.wind.georss",
            "b2b.gtfs",
            "b2b.borderdelays.geojson",
            "b2b.borderdelays.json",
        ).sorted().forEach {

            println(it)

        }
    }
}
