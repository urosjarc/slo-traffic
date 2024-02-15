package com.urosjarc.com.urosjarc.slotraffic

import com.urosjarc.slotraffic.domain.Cameras
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
import javax.xml.bind.JAXBContext


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

    suspend fun getCameras() {
        val res = getData("b2b.cameras.datexii33")
        val context = JAXBContext.newInstance(Cameras::class.java)
        val inputStream = res.bodyAsChannel().toInputStream()
        val cameras = context.createUnmarshaller().unmarshal(inputStream) as Cameras
        println(cameras.lang)
    }
}
