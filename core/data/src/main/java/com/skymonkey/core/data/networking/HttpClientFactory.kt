package com.skymonkey.core.data.networking

import com.skymonkey.core.data.BuildConfig
import com.skymonkey.core.domain.auth.AuthInfo
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.auth.SessionStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
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

class HttpClientFactory(
    val sessionStorage: SessionStorage
) {
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
            install(Auth) { // setup token refresh mechanisms
                bearer {
                    loadTokens {
                        val info = sessionStorage.get()
                        BearerTokens(
                            accessToken = info?.accessToken ?: "",
                            refreshToken = info?.refreshToken ?: ""
                        )
                    }
                    refreshTokens { // invoked if our status is 401
                        val info = sessionStorage.get()
                        // fetch a new access token, using refresh token
                        val response = client.post<AccessTokenRequest, AccessTokenResponse>(
                            route = "/accessToken",
                            body = AccessTokenRequest(
                                refreshToken = info?.refreshToken ?: "",
                                userId = info?.userId ?: ""
                            )
                        )

                        // if successful, save new access token to local storage and return
                        // updated bearer token
                        if (response is Result.Success) {
                            val newAuthInfo = AuthInfo(
                                accessToken = response.data.accessToken,
                                refreshToken = info?.refreshToken ?: "",
                                userId = info?.userId ?: ""
                            )

                            sessionStorage.set(newAuthInfo)

                            BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        } else {
                            BearerTokens(
                                accessToken = "",
                                refreshToken = ""
                            )
                        }
                    }
                }
            }
        }
    }
}