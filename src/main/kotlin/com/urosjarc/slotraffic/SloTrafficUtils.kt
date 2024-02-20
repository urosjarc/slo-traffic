package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.exceptions.AuthException
import com.urosjarc.slotraffic.exceptions.DecodingException
import com.urosjarc.slotraffic.exceptions.ServiceException
import com.urosjarc.slotraffic.res.AuthRes
import com.urosjarc.slotraffic.res.GeoJson
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
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

abstract class SloTrafficUtils(
    val username: String,
    val password: String
) {

    var authRes: AuthRes

    @OptIn(ExperimentalSerializationApi::class)
    val jsonModule = Json {
        allowSpecialFloatingPointValues = false
        allowStructuredMapKeys = false
        coerceInputValues = false
        encodeDefaults = false
        explicitNulls = true
        ignoreUnknownKeys = false
        isLenient = false
        prettyPrint = true
        useAlternativeNames = false
        useArrayPolymorphism = false
    }

    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(jsonModule) }
        install(HttpTimeout) {
            requestTimeoutMillis = 100000
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

    fun url(path: String): String = "https://b2b.nap.si${path}"

    internal suspend fun getReq(name: String): HttpResponse =
        client.get(url("/data/$name")) {
            header("Authorization", "Bearer ${authRes.access_token}")
        }
    internal suspend inline fun <reified T, reified P> getGeoJson(name: String): GeoJson<T, P> {
        val res = this.getReq(name = name)

        if (res.status.value !in 200..299)
            throw ServiceException("Service unavailable: $name")

        val text = res.bodyAsText(fallbackCharset = Charsets.UTF_8).removePrefix(prefix = "\uFEFF")

        try {
            return this.jsonModule.decodeFromString(text)
        } catch (e: Throwable){
            throw DecodingException("Could not decode service response: '$name'", cause = e)
        }
    }

}
