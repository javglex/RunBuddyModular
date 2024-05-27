package com.skymonkey.run.domain

import com.skymonkey.core.domain.location.LocationTimestamp
import kotlin.time.Duration

/**
 * Used for tracking run history.
 * @param distanceMeters - the distance the user has ran, in meters
 * @param pace - run pace
 * @param locations - nested list of tracked locations. sublist tracks a series of location data
 * and represents it as a line. The outer list tracks different lines a user may create by pausing tracked run
 * and moving to a new location before resuming the tracking.
 */
data class RunData(
    val distanceMeters: Int = 0,
    val pace: Duration = Duration.ZERO,
    val locations: List<List<LocationTimestamp>> = emptyList(),
)
