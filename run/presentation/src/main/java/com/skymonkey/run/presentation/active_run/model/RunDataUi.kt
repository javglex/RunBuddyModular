package com.skymonkey.run.presentation.active_run.model

import com.skymonkey.core.domain.location.LocationTimestamp
import kotlin.time.Duration

/**
 * represents run history for the run overview UI card.
 * @param distanceMeters - the distance the user has ran, in meters. Used to upsert run data.
 * @param distance - the distance the user has ran, either in km or mi
 * @param pace - run pace
 * @param locations - nested list of tracked locations. sublist tracks a series of location data
 * @param heartRates - track heart rates to get an average value
 * and represents it as a line. The outer list tracks different lines a user may create by pausing tracked run
 * and moving to a new location before resuming the tracking.
 */
data class RunDataUi(
    val distanceMeters: Int = 0,
    val distance: String = "",
    val pace: String = "",
    val locations: List<List<LocationTimestamp>> = emptyList(),
    val heartRates: List<Int> = emptyList()
)
