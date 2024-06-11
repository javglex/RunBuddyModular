package com.skymonkey.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.core.connectivity.domain.messaging.MessagingAction
import com.skymonkey.core.domain.Result
import com.skymonkey.wear.run.domain.ExerciseTracker
import com.skymonkey.wear.run.domain.PhoneConnector
import com.skymonkey.wear.run.domain.RunningTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector,
    private val runningTracker: RunningTracker
): ViewModel() {

    var state by mutableStateOf(TrackerState())
        private set

    private val hasBodyPermission = MutableStateFlow(false)
    private val isTracking = snapshotFlow {
        state.isRunActive && state.isTrackable && state.isConnectedPhoneNearby
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    private val eventChannel = Channel<TrackerEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        phoneConnector
            .connectedNode
            .filterNotNull()
            .onEach { node ->
                state = state.copy(
                    isConnectedPhoneNearby = node.isNearby
                )
            }
            .combine(isTracking) { _, isTracking ->
                if(!isTracking) {
                    phoneConnector.sendActionToPhone(MessagingAction.ConnectionRequest)
                }
            }
            .launchIn(viewModelScope)

        runningTracker
            .isTrackable
            .onEach { isTrackable ->
                state = state.copy(isTrackable = isTrackable)
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                val result = when {
                    isTracking && !state.hasStartedRunning -> { // we just hit play for the first time
                        exerciseTracker.startExercise()
                    }
                    isTracking && state.hasStartedRunning -> {
                        exerciseTracker.resumeExercise()
                    }
                    !isTracking && state.hasStartedRunning -> {
                        exerciseTracker.pauseExercise()
                    }
                    else -> Result.Success(Unit)
                }

                if (result is Result.Error) {
                    result.error.toUiText()?.let {
                        eventChannel.send(TrackerEvent.Error(it))
                    }
                }

                if(isTracking) {
                    state = state.copy(hasStartedRunning = true)
                }
                runningTracker.setIsTracking(isTracking)
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            val isHeartRateSupported = exerciseTracker.isHeartRateTrackingSupported()
            state = state.copy(canTrackHeartRate = isHeartRateSupported)
        }

        runningTracker
            .heartRate
            .onEach {
                state = state.copy(heartRate = it)
            }
            .launchIn(viewModelScope)

        runningTracker
            .distanceMeters
            .onEach {
                state = state.copy(distanceMeters = it)
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedDuration = it)
            }
            .launchIn(viewModelScope)

        listenToPhoneActions()

    }

    /**
     * Handle actions triggered by our watch (e.g UI button start/pause)
     */
    fun onAction(action: TrackerAction) {
        sendActionToPhone(action)

        when(action) {
            is TrackerAction.OnBodySensorPermissionResult -> {
                hasBodyPermission.value = action.isGranted
                if (action.isGranted) {
                    startTrackingExercise()
                }
            }
            TrackerAction.OnFinishRunClick -> {
                viewModelScope.launch {
                    exerciseTracker.stopExercise()
                    eventChannel.send(TrackerEvent.RunFinished)

                    state = state.copy( // reset our state
                        elapsedDuration = Duration.ZERO,
                        distanceMeters = 0,
                        heartRate = 0,
                        hasStartedRunning = false,
                        isRunActive = false
                    )
                }
            }
            TrackerAction.OnToggleRunClick -> {
                if(state.isTrackable) {
                    state = state.copy(
                        isRunActive = !state.isRunActive
                    )
                }
            }
        }
    }

    /**
     * Handle actions triggered by our phone (e.g events sent to finish workout)
     */
    private fun onPhoneAction(action: TrackerAction) {
        when(action) {
            TrackerAction.OnFinishRunClick -> {
                viewModelScope.launch {
                    exerciseTracker.stopExercise()
                    eventChannel.send(TrackerEvent.RunFinished)

                    state = state.copy( // reset our state
                        elapsedDuration = Duration.ZERO,
                        distanceMeters = 0,
                        heartRate = 0,
                        hasStartedRunning = false,
                        isRunActive = false
                    )
                }
            }
            TrackerAction.OnToggleRunClick -> {
                if(state.isTrackable) {
                    state = state.copy(
                        isRunActive = !state.isRunActive
                    )
                }
            }
            else -> Unit
        }
    }

    private fun startTrackingExercise() {
        viewModelScope.launch {
            val isHeartRateTrackingSupported = exerciseTracker.isHeartRateTrackingSupported()
            state = state.copy(
                canTrackHeartRate = isHeartRateTrackingSupported
            )
        }
    }

    private fun sendActionToPhone(action: TrackerAction) {
        viewModelScope.launch {
            val messagingAction = when(action) {
                is TrackerAction.OnFinishRunClick -> {
                    MessagingAction.Finish
                }
                is TrackerAction.OnToggleRunClick -> {
                    if(state.isRunActive)
                        MessagingAction.Pause
                    else
                        MessagingAction.StartOrResume
                }
                else -> null
            }

            messagingAction?.let {
                val result = phoneConnector.sendActionToPhone(it)
                if (result is Result.Error) {
                    if (BuildConfig.DEBUG) {
                        println("tracker error: ${result.error}") //TODO replace by timber
                    }
                }
            }
        }
    }

    private fun listenToPhoneActions() {
        phoneConnector
            .messagingActions
            .onEach { action ->
                when(action) {
                    MessagingAction.Pause -> {
                        if(state.isTrackable) {
                            state = state.copy(isRunActive = false)
                        }
                    }
                    MessagingAction.Finish -> {
                        onPhoneAction(TrackerAction.OnFinishRunClick)
                    }
                    MessagingAction.StartOrResume -> {
                        if(state.isTrackable) {
                            state = state.copy(isRunActive = true)
                        }
                    }
                    MessagingAction.Trackable -> {
                        state = state.copy(isTrackable = true)
                    }
                    MessagingAction.Untrackable -> {
                        state = state.copy(isTrackable = false)
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }
}