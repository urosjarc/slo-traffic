package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.exceptions.DecodingException
import com.urosjarc.slotraffic.exceptions.NapException
import com.urosjarc.slotraffic.res.GeoJson
import com.urosjarc.slotraffic.res.NapAuthRes
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
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class NapApi(
    val username: String,
    val password: String
) {
    var authRes: NapAuthRes

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

    fun url(path: String): String = "https://b2b.nap.si${path}"

    @OptIn(InternalAPI::class)
    private suspend fun login(): NapAuthRes {
        val res = client.post(url("/uc/user/token")) {
            this.body = FormDataContent(Parameters.build {
                append("grant_type", "password")
                append("username", username)
                append("password", password)
            })
        }

        if (res.status.value !in 200..299)
            throw NapException("Could not authenticate nap user: $res")

        return res.body<NapAuthRes>()
    }


    suspend inline fun <reified T, reified P> getGeoJson(name: String): GeoJson<T, P> {
        val res = client.get(url("/data/$name")) {
            header("Authorization", "Bearer ${authRes.access_token}")
        }

        if (res.status.value !in 200..299)
            throw NapException("Service unavailable: $name")

        val text = res.bodyAsText(fallbackCharset = Charsets.UTF_8).removePrefix(prefix = "\uFEFF")

        try {
            return this.jsonModule.decodeFromString(text)
        } catch (e: Throwable) {
            throw DecodingException("Could not decode service response: '$name'", cause = e)
        }
    }

    suspend fun getBigFile(name: String): MutableList<Byte> {
        val payload = mutableListOf<Byte>()
        var bytesCount = 0.0
        client.prepareGet(url("/data/$name")) {
            header("Authorization", "Bearer ${authRes.access_token}")
        }.execute { res ->
            if (res.status.value !in 200..299)
                throw NapException("Service unavailable: $name")

            val channel: ByteReadChannel = res.body()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes: ByteArray = packet.readBytes()
                    payload.addAll(bytes.toList())
                    bytesCount += bytes.size
                }
            }
        }

        return payload
    }

    suspend fun getZipFile(name: String, onZipEntry: (zEntry: ZipEntry, inStream: InputStream) -> Unit) {
        val bytes = this.getBigFile(name = name)
        val inZipStream = ByteArrayInputStream(bytes.toByteArray())
        val zStream = ZipInputStream(inZipStream)
        while (true) {
            val zEntry = zStream.nextEntry ?: break
            val outStream = ByteArrayOutputStream()
            zStream.copyTo(outStream)
            val inStream = ByteArrayInputStream(outStream.toByteArray())
            onZipEntry(zEntry, inStream)
        }
    }
}
