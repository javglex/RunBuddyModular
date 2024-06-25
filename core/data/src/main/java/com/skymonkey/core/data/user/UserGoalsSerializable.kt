package com.skymonkey.core.data.user

import kotlinx.serialization.Serializable

@Serializable
class UserGoalsSerializable(
    val distanceGoal: Double,
    val goalFrequency: Int
)
