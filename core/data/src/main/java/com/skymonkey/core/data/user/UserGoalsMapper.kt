package com.skymonkey.core.data.user

import com.skymonkey.core.data.auth.AuthInfoSerializable
import com.skymonkey.core.domain.auth.AuthInfo
import com.skymonkey.core.domain.user.UserGoals

fun UserGoals.toUserGoalsSerializable(): UserGoalsSerializable =
    UserGoalsSerializable(
        goalFrequency = goalFrequency,
        distanceGoal = distanceGoal
    )

fun UserGoalsSerializable.toUserGoals(): UserGoals =
    UserGoals(
        goalFrequency = goalFrequency,
        distanceGoal = distanceGoal
    )
