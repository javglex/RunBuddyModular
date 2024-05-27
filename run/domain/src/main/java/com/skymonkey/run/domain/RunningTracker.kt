package com.skymonkey.run.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    private val isObservingLocation = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
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

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }
}