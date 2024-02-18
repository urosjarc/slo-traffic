package com.urosjarc.slotraffic

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.File

abstract class SloTrafficUtils(
    val username: String,
    val password: String
) {

    private var authRes: AuthRes

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 1000000
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
        runBlocking(Dispatchers.IO) {
            authRes = login()
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun login(): AuthRes {
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

    private fun url(path: String): String = "https://b2b.nap.si${path}"

    internal suspend fun getData(name: String): HttpResponse =
        client.get(url("/data/$name")) { header("Authorization", "Bearer ${authRes.access_token}") }

    internal suspend fun getXmlData(name: String, cb: (doc: Document) -> Unit) {
        val res0 = getData(name = name)
        File("weather.xml").writeText(res0.bodyAsText())

        val res = getData(name = name)
        val inputStream = res.bodyAsChannel().toInputStream()
        val doc: Document = Jsoup.parse(inputStream, null, "", Parser.xmlParser())
        cb(doc)
    }

}
