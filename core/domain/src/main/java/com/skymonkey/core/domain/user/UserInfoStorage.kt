package com.skymonkey.core.domain.user

/**
 * Used for storing user set info and goals in local disk.
 */
interface UserInfoStorage {
    /**
     * Fetches user goals stored in disk.
     * @return [UserGoals] object containing distance and time goals
     */
    suspend fun getGoals(): UserGoals?

    suspend fun setGoals(userGoals: UserGoals?)

    /**
     * Fetches units setting to determine whether to display imperial or metric units.
     */
    suspend fun getMetricUnitSetting(): Boolean
    suspend fun setMetricUnitSetting(enabled: Boolean)

}
