package com.skymonkey.auth.data

import com.skymonkey.auth.domain.AuthRepository
import com.skymonkey.core.data.networking.post
import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.asEmptyDataResult
import com.skymonkey.core.domain.auth.AuthInfo
import com.skymonkey.core.domain.auth.SessionStorage
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthRepository {
    override suspend fun register(
        email: String,
        password: String
    ): EmptyResult<DataError.Network> =
        httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body =
                RegisterRequest(
                    email = email,
                    password = password
                )
        )

    override suspend fun login(
        email: String,
        password: String
    ): EmptyResult<DataError.Network> {
        val result =
            httpClient.post<LoginRequest, LoginResponse>(
                route = "/login",
                body =
                    LoginRequest(
                        email = email,
                        password = password
                    )
            )
        if (result is Result.Success) {
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )
        }

        return result.asEmptyDataResult()
    }
}
