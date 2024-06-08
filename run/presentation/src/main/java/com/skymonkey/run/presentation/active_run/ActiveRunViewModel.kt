package com.skymonkey.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.location.Location
import com.skymonkey.core.domain.location.LocationTimestamp
import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.domain.run.RunRepository
import com.skymonkey.core.presentation.ui.asUiText
import com.skymonkey.run.domain.LocationDataCalculator
import com.skymonkey.run.domain.RunData
import com.skymonkey.run.domain.RunningTracker
import com.skymonkey.run.presentation.active_run.service.ActiveRunService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository
): ViewModel() {
    private var previousLocations: List<List<LocationTimestamp>> = emptyList()

    var state by mutableStateOf(ActiveRunState(
        shouldTrack = ActiveRunService.isServiceActive && runningTracker.isTracking.value,
        hasStartedRunning = ActiveRunService.isServiceActive,
    ))
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val shouldTrack = snapshotFlow {
        state.shouldTrack
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        state.shouldTrack
    )

    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { shouldTrack, hasPermission ->
        shouldTrack && hasPermission
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    init {
        hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }.launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)

        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(currentLocation = it?.location)
            }
            .launchIn(viewModelScope)

        runningTracker
            .runData
            .flatMapConcat { runData ->
                throttleRunData(
                    runData.locations,
                    previousLocations,
                    loading = {
                        state = state.copy(showLoadingChunks = true)
                    }, finished = {
                        state = state.copy(showLoadingChunks = false)
                    }).map { locations ->
                        runData.copy(locations = locations)
                    }
            }
            .onEach { runData ->
                previousLocations = runData.locations
                state = state.copy(runData = runData)
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedTime = it)
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {
        when(action) {
            ActiveRunAction.OnFinishRunClick -> {
                state = state.copy(
                    isRunFinished = true,
                    isSaving = true
                )
            }
            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(
                    shouldTrack = true
                )
            }
            ActiveRunAction.OnBackClick -> {
                state = state.copy(
                    shouldTrack = false
                )
            }
            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocationPermission
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }
            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationRationale = action.showNotificationRationale
                )
            }
            is ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showNotificationRationale = false,
                    showLocationRationale = false
                )
            }
            is ActiveRunAction.OnRunProcessed -> {
                finishRun(action.mapPictureBytes)
            }
            else -> {}
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        previousLocations = listOf() // clear cached locations

        val locations = state.runData.locations
        // if we have no locations or only one location, cancel
        if(locations.isEmpty() || locations.first().size <= 1) {
            state = state.copy(isSaving = false)
            return
        }

        viewModelScope.launch {
            val run = Run(
                id = null,
                duration = state.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneId.of("UTC")),
                distanceMeters = state.runData.distanceMeters,
                location = state.currentLocation ?: Location(0.0, 0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null
            )

            runningTracker.finishRun()

            when(val result = runRepository.upsertRun(run, mapPictureBytes)) {
                is Result.Error -> eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                is Result.Success -> {
                    eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }

            state = state.copy(isSaving = false, showLoadingChunks = false)
        }
    }

    /*
    Throttles the location dataset that is sent to the Active Run Screen.
    This function is necessary to prevent overwhelming and crashing the Google Map composable if
    it was reconstructing the polylines from scratch at once.
    It will only throttle location data that hasn't been previously throttled before.
    So if new location data comes we don't throttle the entire list again.
     */
    private fun throttleRunData(
        locations: List<List<LocationTimestamp>>,
        prevLocations: List<List<LocationTimestamp>>,
        loading: () -> Unit,
        finished: () -> Unit,
    ): Flow<List<List<LocationTimestamp>>> = flow {
        val chunkSize = 20
        val outerList = mutableListOf<List<LocationTimestamp>>()
        for ((nextIndex, innerList) in locations.withIndex()) {
            // populate outer list with previously throttled location list data
            val currentInnerList = prevLocations.getOrNull(nextIndex)?.toMutableList() ?: mutableListOf()
            outerList.add(currentInnerList)
            val newItems = innerList.filter { it !in currentInnerList}
            if (nextIndex == locations.size - 1 && newItems.size <= chunkSize) { //if we're within our last chunk, consider finished.
                finished()
            } else {
                loading()
            }
            newItems.chunked(chunkSize).forEach { chunk ->
                currentInnerList.addAll(chunk)
                outerList[nextIndex] = currentInnerList
                emit(outerList.map { it.toList() })  // Emit a copy of the current state
                delay(10L)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive) {
            runningTracker.stopObservingLocation()
        }
    }
}