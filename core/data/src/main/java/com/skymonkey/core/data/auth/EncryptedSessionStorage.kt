package com.skymonkey.core.data.auth

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.skymonkey.core.domain.auth.AuthInfo
import com.skymonkey.core.domain.auth.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncryptedSessionStorage(
    private val sharedPreferences: SharedPreferences
) : SessionStorage {
    override suspend fun get(): AuthInfo? =
        withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(KEY_AUTH_INFO, null)
            json?.let {
                Json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo()
            }
        }

    @SuppressLint("ApplySharedPref")
    override suspend fun set(info: AuthInfo?) {
        withContext(Dispatchers.IO) {
            if (info == null) {
                sharedPreferences.edit().remove(KEY_AUTH_INFO).commit()
                return@withContext
            }

            val json = Json.encodeToString(info.toAuthInfoSerializable())
            sharedPreferences
                .edit()
                .putString(KEY_AUTH_INFO, json)
                .commit()
        }
    }

    companion object {
        private const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }
}
