package com.skymonkey.core.data.networking

import com.skymonkey.core.data.BuildConfig
import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Extension functions for Ktor.
 * Ktor comes with built in plugins for authentication (e.g token refresh).
 * Ktor is also pure kotlin. Can be used in KMM.
 */

suspend inline fun <reified Response : Any?> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> =
    safeCall {
        get {
            url(constructRoute(route))
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }

suspend inline fun <reified Response : Any?> HttpClient.delete(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> =
    safeCall {
        delete {
            url(constructRoute(route))
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }

suspend inline fun <reified Request, reified Response : Any?> HttpClient.post(
    route: String,
    body: Request
): Result<Response, DataError.Network> =
    safeCall {
        post {
            url(constructRoute(route))
            setBody(body)
        }
    }

/**
 * As opposed to just calling post from the http client, this extends from the RefreshTokenParams in order to tell the HttpRequestBuilder
 * that we are requesting a token refresh. This allows us to catch 401 without getting into a loop.
 */
suspend inline fun <reified Request, reified Response : Any?> RefreshTokensParams.postTokenRefresh(
    client: HttpClient,
    route: String,
    body: Request
): Result<Response, DataError.Network> =
    safeCall {
        client.post {
            markAsRefreshTokenRequest()
            url(constructRoute(route))
            setBody(body)
        }
    }

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response =
        try {
            execute()
        } catch (e: UnresolvedAddressException) {
            // if no network, or invalid url
            e.printStackTrace()
            return Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: SerializationException) {
            e.printStackTrace()
            return Result.Error(DataError.Network.SERIALIZATION)
        } catch (e: Exception) {
            // careful when catching general exceptions inside a suspend function.
            // CancellationException is used to cancel coroutine jobs
            // to fix this, here we rethrow CancellationException if caught.
            if (e is CancellationException) throw e
            e.printStackTrace()
            return Result.Error(DataError.Network.UNKNOWN)
        }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> =
    when (response.status.value) {
        in 200..299 -> Result.Success(response.body<T>())
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }

fun constructRoute(route: String): String =
    when {
        route.contains(BuildConfig.BASE_URL) -> route
        route.startsWith("/") -> BuildConfig.BASE_URL + route
        else -> BuildConfig.BASE_URL + "/$route"
    }
