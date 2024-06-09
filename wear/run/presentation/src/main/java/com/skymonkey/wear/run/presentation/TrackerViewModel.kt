package com.skymonkey.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.wear.run.domain.ExerciseTracker
import com.skymonkey.wear.run.domain.PhoneConnector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector
): ViewModel() {

    var state by mutableStateOf(TrackerState())
        private set

    private val hasBodyPermission = MutableStateFlow(false)

    init {
        phoneConnector
            .connectedNode
            .filterNotNull()
            .onEach { node ->
                state = state.copy(
                    isConnectedPhoneNearby = node.isNearby
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: TrackerAction) {
        when(action) {
            is TrackerAction.OnBodySensorPermissionResult -> {
                hasBodyPermission.value = action.isGranted
                if (action.isGranted) {
                    startTrackingExercise()
                }
            }
            TrackerAction.OnFinishRunClick -> Unit
            TrackerAction.OnToggleRunClick -> Unit
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
}