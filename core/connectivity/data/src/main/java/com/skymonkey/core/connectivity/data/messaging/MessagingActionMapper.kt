package com.skymonkey.core.connectivity.data.messaging

import com.skymonkey.core.connectivity.domain.messaging.MessagingAction

fun MessagingAction.toMessagingActionDto(): MessagingActionDto =
    when (this) {
        MessagingAction.ConnectionRequest -> MessagingActionDto.ConnectionRequest
        is MessagingAction.DistanceUpdate -> MessagingActionDto.DistanceUpdate(distanceMeters)
        MessagingAction.Finish -> MessagingActionDto.Finish
        is MessagingAction.HeartRateUpdate -> MessagingActionDto.HeartRateUpdate(heartRate)
        MessagingAction.Pause -> MessagingActionDto.Pause
        MessagingAction.StartOrResume -> MessagingActionDto.StartOrResume
        is MessagingAction.TimeUpdate -> MessagingActionDto.TimeUpdate(elapsedDuration)
        MessagingAction.Trackable -> MessagingActionDto.Trackable
        MessagingAction.Untrackable -> MessagingActionDto.Untrackable
        is MessagingAction.CaloriesUpdate -> MessagingActionDto.CaloriesUpdate(calories)
    }

fun MessagingActionDto.toMessagingAction(): MessagingAction =
    when (this) {
        MessagingActionDto.ConnectionRequest -> MessagingAction.ConnectionRequest
        is MessagingActionDto.DistanceUpdate -> MessagingAction.DistanceUpdate(distanceMeters)
        MessagingActionDto.Finish -> MessagingAction.Finish
        is MessagingActionDto.HeartRateUpdate -> MessagingAction.HeartRateUpdate(heartRate)
        MessagingActionDto.Pause -> MessagingAction.Pause
        MessagingActionDto.StartOrResume -> MessagingAction.StartOrResume
        is MessagingActionDto.TimeUpdate -> MessagingAction.TimeUpdate(elapsedDuration)
        MessagingActionDto.Trackable -> MessagingAction.Trackable
        MessagingActionDto.Untrackable -> MessagingAction.Untrackable
        is MessagingActionDto.CaloriesUpdate -> MessagingAction.CaloriesUpdate(calories)
    }
