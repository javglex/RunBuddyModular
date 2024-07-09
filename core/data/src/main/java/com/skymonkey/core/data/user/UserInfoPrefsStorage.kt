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
            val json = sharedPreferences.getString(USER_GOALS_KEY, null)
            json?.let {
                Json.decodeFromString<UserGoalsSerializable>(it).toUserGoals()
            }
        }

    @SuppressLint("ApplySharedPref")
    override suspend fun setGoals(userGoals: UserGoals?) {
        withContext(Dispatchers.IO) {
            if (userGoals == null) {
                sharedPreferences.edit().remove(USER_GOALS_KEY).commit()
                return@withContext
            }

            val json = Json.encodeToString(userGoals.toUserGoalsSerializable())
            sharedPreferences
                .edit()
                .putString(USER_GOALS_KEY, json)
                .commit()
        }
    }

    override suspend fun getMetricUnitSetting(): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext sharedPreferences.getBoolean(METRIC_UNIT_SETTING, false)
        }

    override suspend fun setMetricUnitSetting(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            sharedPreferences
                .edit()
                .putBoolean(METRIC_UNIT_SETTING, enabled)
                .commit()
        }
    }


    companion object {
        private const val USER_GOALS_KEY = "USER_GOALS_KEY"
        private const val METRIC_UNIT_SETTING = "METRIC_UNIT_SETTING"
    }
}
