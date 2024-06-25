package com.skymonkey.core.data.user

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.skymonkey.core.domain.user.UserGoals
import com.skymonkey.core.domain.user.UserInfoStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserInfoPrefsStorage(
    private val sharedPreferences: SharedPreferences
) : UserInfoStorage {
    override suspend fun getGoals(): UserGoals? =
        withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(KEY_USER_GOALS, null)
            json?.let {
                Json.decodeFromString<UserGoalsSerializable>(it).toUserGoals()
            }
        }

    @SuppressLint("ApplySharedPref")
    override suspend fun setGoals(userGoals: UserGoals?) {
        withContext(Dispatchers.IO) {
            if (userGoals == null) {
                sharedPreferences.edit().remove(KEY_USER_GOALS).commit()
                return@withContext
            }

            val json = Json.encodeToString(userGoals.toUserGoalsSerializable())
            sharedPreferences
                .edit()
                .putString(KEY_USER_GOALS, json)
                .commit()
        }
    }

    companion object {
        private const val KEY_USER_GOALS = "KEY_USER_GOALS"
    }
}
