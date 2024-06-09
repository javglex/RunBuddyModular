package com.skymonkey.core.connectivity.data.messaging

import com.skymonkey.core.connectivity.domain.messaging.MessagingAction
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable // TODO, also tag data objects as serializable?
sealed interface MessagingActionDto {
    data object StartOrResume: MessagingActionDto
    data object Pause: MessagingActionDto
    data object Finish: MessagingActionDto
    data object Trackable: MessagingActionDto
    data object Untrackable: MessagingActionDto
    data object ConnectionRequest: MessagingActionDto
    data class HeartRateUpdate(val heartRate: Int): MessagingActionDto
    data class DistanceUpdate(val distanceMeters: Int): MessagingActionDto
    data class TimeUpdate(val elapsedDuration: Duration): MessagingActionDto
}