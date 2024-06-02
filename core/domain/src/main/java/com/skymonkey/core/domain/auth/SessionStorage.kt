package com.skymonkey.core.domain.auth

import com.skymonkey.core.domain.auth.AuthInfo

/**
 * Used for storing authentication info in local disk.
 */
interface SessionStorage {
    /**
     * Fetches auth info stored in disk.
     * @return [AuthInfo] object containing access & refresh tokens
     */
    suspend fun get(): AuthInfo?

    /**
     * Saves auth info to disk.
     * @param info [AuthInfo] data to store in disk. Set to null to clear data.
     */
    suspend fun set(info: AuthInfo?)
}