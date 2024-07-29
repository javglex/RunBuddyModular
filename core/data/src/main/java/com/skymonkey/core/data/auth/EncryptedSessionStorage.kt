package com.skymonkey.core.data.auth

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.skymonkey.core.domain.auth.AuthInfo
import com.skymonkey.core.domain.auth.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncryptedSessionStorage(
    private val sharedPreferences: SharedPreferences
) : SessionStorage {

    private val _authInfoFlow = MutableStateFlow<AuthInfo?>(null)

    override suspend fun get(): StateFlow<AuthInfo?> {
        _authInfoFlow.value = withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(KEY_AUTH_INFO, null)
            json?.let {
                Json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo()
            }
        }

        return _authInfoFlow
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun set(info: AuthInfo?) {
        withContext(Dispatchers.IO) {
            if (info == null) {
                sharedPreferences.edit().remove(KEY_AUTH_INFO).commit()
                _authInfoFlow.value = null
                return@withContext
            }

            val json = Json.encodeToString(info.toAuthInfoSerializable())
            sharedPreferences
                .edit()
                .putString(KEY_AUTH_INFO, json)
                .commit()

            _authInfoFlow.value = info
        }
    }

    companion object {
        private const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }
}
