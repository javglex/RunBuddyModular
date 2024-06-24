package com.skymonkey.run.domain

import com.skymonkey.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {
    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int =
        locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine
                    .zipWithNext { locationA, locationB ->
                        locationA.locationWithAltitude.location.distanceTo(locationB.locationWithAltitude.location)
                    }.sum()
                    .roundToInt()
            }

    fun getMaxSpeedKmh(locations: List<List<LocationTimestamp>>): Double =
        locations.maxOf { locationSet ->
            locationSet
                .zipWithNext { locationA, locationB ->
                    val distance =
                        locationA.locationWithAltitude.location.distanceTo(
                            other = locationB.locationWithAltitude.location
                        )
                    val hoursDifference =
                        (locationB.durationTimeStamp - locationA.durationTimeStamp)
                            .toDouble(DurationUnit.HOURS)
                    if (hoursDifference == 0.0) {
                        0.0
                    } else {
                        (distance / 1000.0) / hoursDifference
                    }
                }.maxOrNull() ?: 0.0
        }

    /**
     * Add up all positive altitude changes. Ignore negative altitude changes.
     */
    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int =
        locations.sumOf { locationSet ->
            locationSet
                .zipWithNext { locationA, locationB ->
                    val altitudeA = locationA.locationWithAltitude.altitude
                    val altitudeB = locationB.locationWithAltitude.altitude
                    (altitudeB - altitudeA).coerceAtLeast(0.0)
                }.sum()
                .roundToInt()
        }
}
