package com.urosjarc.slotraffic

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertTrue

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
        val data = client.getCameras()
        assertTrue(data.isNotEmpty())
        data.forEach {
            assertTrue(it.imgUrl.isNotBlank())
            assertTrue(it.title.isNotEmpty())
            assertTrue(it.description.isNotEmpty())
            assertTrue(it.group.isNotEmpty())
            assertTrue(it.region.isNotEmpty())
        }
    }

    @Test
    fun `test get counters`(): Unit = runBlocking {
        val data = client.getCounters()
        assertTrue(data.isNotEmpty())
    }

    @Test
    fun `test get events`(): Unit = runBlocking {
        val data = client.getEvents()
        assertTrue(data.isNotEmpty())
        data.forEach {
            assertTrue(it.comment.isNotEmpty())
        }
    }

    @Test
    fun `test get rest areas`(): Unit = runBlocking {
        val data = client.getRestAreas()
        assertTrue(data.isNotEmpty())

        val hasTitle = mutableListOf<Boolean>()
        val hasDesc = mutableListOf<Boolean>()
        val hasFaci = mutableListOf<Boolean>()
        data.forEach {
            hasTitle.add(it.title.isNotEmpty())
            hasDesc.add(it.description.isNotEmpty())
            hasFaci.add(it.facilities.isNotEmpty())
        }
        assertTrue(hasTitle.contains(true))
        assertTrue(hasDesc.contains(true))
        assertTrue(hasFaci.contains(true))
    }

    @Test
    fun `test get roadworks`(): Unit = runBlocking {
        val data = client.getRoadWorks()
        assertTrue(data.isNotEmpty())
        data.forEach {
            assertTrue(it.comment.isNotEmpty())
        }
    }

    @Test
    fun `test get weather`(): Unit = runBlocking {
        val data = client.getWeather()
        assertTrue(data.wind.isNotEmpty())
        assertTrue(data.temperature.isNotEmpty())
        assertTrue(data.humidity.isNotEmpty())
        assertTrue(data.visibility.isNotEmpty())
        assertTrue(data.roadSurface.isNotEmpty())
    }
}
