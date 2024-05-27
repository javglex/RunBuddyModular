package com.skymonkey.run.domain

import com.skymonkey.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt

object LocationDataCalculator {

    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine.zipWithNext { locationA, locationB ->
                    locationA.locationWithAltitude.location.distanceTo(locationB.locationWithAltitude.location)
                }.sum().roundToInt()
            }
    }
}