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

}
