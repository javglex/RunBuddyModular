package com.skymonkey.auth.data

import com.skymonkey.auth.domain.AuthRepository
import com.skymonkey.core.data.networking.post
import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
): AuthRepository {
    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password
            )
        )
    }
}