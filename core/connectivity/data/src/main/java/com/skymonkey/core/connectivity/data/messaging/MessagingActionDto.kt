package com.skymonkey.core.connectivity.data.messaging

import com.skymonkey.core.connectivity.domain.messaging.MessagingAction
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable // TODO, also tag data objects as serializable?
sealed interface MessagingActionDto {
    @Serializable
    data object StartOrResume: MessagingActionDto
    @Serializable
    data object Pause: MessagingActionDto
    @Serializable
    data object Finish: MessagingActionDto
    @Serializable
    data object Trackable: MessagingActionDto
    @Serializable
    data object Untrackable: MessagingActionDto
    @Serializable
    data object ConnectionRequest: MessagingActionDto
    @Serializable
    data class HeartRateUpdate(val heartRate: Int): MessagingActionDto
    @Serializable
    data class DistanceUpdate(val distanceMeters: Int): MessagingActionDto
    @Serializable
    data class TimeUpdate(val elapsedDuration: Duration): MessagingActionDto
}