package com.skymonkey.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.core.connectivity.domain.messaging.MessagingAction
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.location.Location
import com.skymonkey.core.domain.location.LocationTimestamp
import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.domain.run.RunRepository
import com.skymonkey.core.presentation.ui.asUiText
import com.skymonkey.run.domain.LocationDataCalculator
import com.skymonkey.run.domain.RunningTracker
import com.skymonkey.run.domain.WatchConnector
import com.skymonkey.core.presentation.service.ActiveRunService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector,
    private val applicationScope: CoroutineScope
): ViewModel() {
    private var previousLocations: List<List<LocationTimestamp>> = emptyList()

    var state by mutableStateOf(ActiveRunState(
        shouldTrack = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
        hasStartedRunning = ActiveRunService.isServiceActive.value,
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

        watchConnector
            .connectedDevice
            .filterNotNull()
            .onEach {
                Timber.i("New device detected: ${it.displayName}")
            }
            .launchIn(viewModelScope)

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

        listenToWatchActions()
    }

    fun onAction(action: ActiveRunAction) {

        // map ui action to message action
        val messagingAction = when(action) {
            ActiveRunAction.OnFinishRunClick -> MessagingAction.Finish
            ActiveRunAction.OnResumeRunClick -> MessagingAction.StartOrResume
            ActiveRunAction.OnToggleRunClick -> {
                if (state.hasStartedRunning) {
                    MessagingAction.Pause
                } else {
                    MessagingAction.StartOrResume
                }
            }
            else -> null
        }

        //send message action to watch
        messagingAction?.let {
            viewModelScope.launch {
                watchConnector.sendActionToWatch(it)
            }
        }

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

    private fun onWatchAction(action: ActiveRunAction) {
        // handle ui action for our view
        when(action) {
            ActiveRunAction.OnFinishRunClick -> {
                state = state.copy(
                    isRunFinished = true,
                    isSaving = true
                )
            }
            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }
            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(
                    shouldTrack = true
                )
            }
            else -> Unit
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
                mapPictureUrl = null,
                avgHeartRate = if(state.runData.heartRates.isEmpty()) {
                    null
                } else {
                    state.runData.heartRates.average().roundToInt()
                },
                maxHeartRate = if(state.runData.heartRates.isEmpty()) {
                    null
                } else {
                    state.runData.heartRates.max()
                }
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

    private fun listenToWatchActions() {
        watchConnector
            .messagingActions
            .onEach { action ->
                when(action) {
                    MessagingAction.ConnectionRequest -> {
                        //if we connect and there is already an ongoing run, update the watch with the correct state
                        if(isTracking.value) {
                            watchConnector.sendActionToWatch(MessagingAction.StartOrResume)
                        }
                    }
                    MessagingAction.Finish -> {
                        onWatchAction(
                            action = ActiveRunAction.OnFinishRunClick
                        )
                    }
                    MessagingAction.Pause -> {
                        if(isTracking.value) {
                            onWatchAction(
                                action = ActiveRunAction.OnToggleRunClick
                            )
                        }
                    }
                    MessagingAction.StartOrResume -> {
                        if(!isTracking.value) {
                            if(state.hasStartedRunning)
                                onWatchAction(ActiveRunAction.OnResumeRunClick)
                            else
                                onWatchAction(ActiveRunAction.OnToggleRunClick)
                        }
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive.value) {
            applicationScope.launch {  // launched in application scope because viewmodel scope will be cleared by now.
                watchConnector.sendActionToWatch(MessagingAction.Untrackable)
            }
            runningTracker.stopObservingLocation()
        }
    }
}