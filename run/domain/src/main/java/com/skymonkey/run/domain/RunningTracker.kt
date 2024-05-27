@file:OptIn(ExperimentalCoroutinesApi::class)

package com.skymonkey.run.domain

import com.skymonkey.core.domain.Timer
import com.skymonkey.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Calculates run data using location and time-elapsed updates.
 * It's treated as a singleton and meant to be used in combination with a foreground service.
 */
class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val isTracking = MutableStateFlow(false) // is the run active?
    private val isObservingLocation = MutableStateFlow(false) // are we listening to locations? e.g not just for tracking runs but also for updating user on the map

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    // maps current location if our isObservingLocation boolean is true
    val currentLocation = isObservingLocation
        .flatMapLatest {  isObservingLocation ->
            if (isObservingLocation){
                locationObserver.observerLocation(1000L)
            } else flowOf()

        }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily, // only start producing if we are listening to it
            null
        )

    init {
        isTracking
            .flatMapLatest {  isTracking ->
                if (isTracking) {
                    Timer.timeAndEmit()
                } else flowOf()
            }
            .onEach {
                _elapsedTime.value += it
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            // combineTransform block triggers when there is either a new location value, or new isTracking value
            .combineTransform(isTracking) { location, isTracking ->
                if(isTracking)
                    emit(location)
            }
            // similar to combine, but will only trigger when there are new values for both location & elapsedTime
            .zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    locationWithAltitude = location,
                    durationTimeStamp = elapsedTime
                )
            }
            .onEach { locationTimeStamp ->
                val currentLocations = runData.value.locations
                // grab our latest location line
                val lastLocationsList = if(currentLocations.isNotEmpty()) {
                    //update our line with newest location
                    currentLocations.last() + locationTimeStamp
                } else listOf(locationTimeStamp) // else create a new line with the newest location

                val newLocationList = currentLocations.replaceLast(lastLocationsList)

                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(
                    locations = newLocationList
                )

                val distanceKm = distanceMeters / 1000.0

                val currentDuration = locationTimeStamp.durationTimeStamp

                val avgSecondsPerKm = if (distanceKm == 0.0) {
                    0
                } else {
                    (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                }

                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationList
                    )
                }
            }
            /*
                using application scope because we treat our class as a singleton/global state.
                eventually this class will go in a foreground service. this will preserve state when going
                through process death.
             */
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        this.isTracking.value = isTracking
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }
}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    if(this.isEmpty()) {
        return listOf(replacement)
    }
    // first drop the last inner list, and then append the new replacement inner-list
    return this.dropLast(1) + listOf(replacement)
}