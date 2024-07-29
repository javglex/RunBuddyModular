package com.skymonkey.core.data.auth

import com.skymonkey.core.data.networking.get
import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.asEmptyDataResult
import com.skymonkey.core.domain.auth.LogoutRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin

class LogoutRepositoryImpl(
    private val client: HttpClient
) : LogoutRepository {
    override suspend fun logout(): EmptyResult<DataError.Network> {
        val result = client.get<Unit>(route = "/logout").asEmptyDataResult()

        // clear auth tokens
        client.plugin(Auth)
            .providers
            .filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken()

        return result
    }
}
