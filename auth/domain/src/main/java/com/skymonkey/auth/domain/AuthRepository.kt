package com.skymonkey.auth.domain

import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String
    ): EmptyResult<DataError.Network>

    suspend fun login(
        email: String,
        password: String
    ): EmptyResult<DataError.Network>
}
