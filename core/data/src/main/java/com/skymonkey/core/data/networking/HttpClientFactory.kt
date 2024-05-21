package com.skymonkey.core.data.networking

import com.skymonkey.core.data.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

class HttpClientFactory {
    fun build(): HttpClient {
        return HttpClient(CIO) { //CIO is an engine similar to okhttp
            install(ContentNegotiation) { // parsing data and converting json
                json(
                    json = Json {
                        ignoreUnknownKeys = true // ignore unknown json fields. prevents crashes
                    }
                )
            }
            install(Logging) { // for debugging
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest { // how standard request looks like. default content type
                contentType(ContentType.Application.Json)
                header("x-api-key", BuildConfig.API_KEY)
            }
        }
    }
}