package com.skymonkey.wear.run.domain

import com.skymonkey.core.connectivity.domain.messaging.MessagingAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class RunningTracker(
    private val watchToPhoneConnector: PhoneConnector,
    private val exerciseTracker: ExerciseTracker,
    applicationScope: CoroutineScope,
) {
    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    private val _calories = MutableStateFlow(0)
    val calories = _calories.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isTrackable = MutableStateFlow(false)
    val isTrackable = _isTrackable.asStateFlow()

    val distanceMeters =
        watchToPhoneConnector
            .messagingActions
            .filterIsInstance<MessagingAction.DistanceUpdate>()
            .map { it.distanceMeters }
            .stateIn(
                scope = applicationScope, // only use application scope if using this class in a foreground service.
                started = SharingStarted.Lazily,
                initialValue = 0
            )

    val elapsedTime =
        watchToPhoneConnector
            .messagingActions
            .filterIsInstance<MessagingAction.TimeUpdate>()
            .map { it.elapsedDuration }
            .stateIn(
                scope = applicationScope, // only use application scope if using this class in a foreground service.
                started = SharingStarted.Lazily,
                initialValue = Duration.ZERO
            )

    init {
        watchToPhoneConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.Trackable -> {
                        _isTrackable.value = true
                    }
                    MessagingAction.Untrackable -> {
                        _isTrackable.value = false
                    }
                    else -> Unit
                }
            }.launchIn(applicationScope) // only use application scope if using this class in a foreground service.

        watchToPhoneConnector
            .connectedNode
            .filterNotNull()
            .onEach {
                exerciseTracker.prepareExercise()
            }.launchIn(applicationScope) // only use application scope if using this class in a foreground service.

        isTracking
            .flatMapLatest { isTracking ->
                if (isTracking) {
                    exerciseTracker.metrics
                } else {
                    flowOf()
                }
            }.onEach { metrics ->
                metrics.heartRate?.let { hr ->
                    watchToPhoneConnector.sendActionToPhone(MessagingAction.HeartRateUpdate(hr))
                    _heartRate.value = hr
                }
                metrics.calories?.let { calories ->
                    watchToPhoneConnector.sendActionToPhone(MessagingAction.CaloriesUpdate(calories))
                    _calories.value = calories
                }
            }.launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }
}
