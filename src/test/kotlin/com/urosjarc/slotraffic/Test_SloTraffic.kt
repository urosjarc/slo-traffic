package com.urosjarc.slotraffic

import io.ktor.client.request.*
import io.ktor.client.statement.*
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
        client.getCameras()
    }

    @Test
    fun `test get roadworks`(): Unit = runBlocking {
        client.getRoadworks()
    }

    @Test
    fun `test get rest areas`(): Unit = runBlocking {
        client.getRestAreas()
    }

    @Test
    fun `test get events`(): Unit = runBlocking {
        client.getEvents()
    }
    @Test
    fun `test get counters`(): Unit = runBlocking {
        client.getCounters()
    }
    @Test
    fun `test get winds`(): Unit = runBlocking {
        client.getWinds()
    }

    @Test
    fun `test get border delays`(): Unit = runBlocking {
        client.getBorderDelays()
    }

}
