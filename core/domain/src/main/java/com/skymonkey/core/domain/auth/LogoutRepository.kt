package com.skymonkey.core.domain.auth

import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult

/**
 * Logging out can be used from multiple locations (and features) in the app.
 * It's why it's not in our auth module inside the AuthRepository.
 * Still hard to justify this approach, but will go along with it for now.
 */
interface LogoutRepository {
    suspend fun logout(): EmptyResult<DataError.Network>
}
