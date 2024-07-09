package com.skymonkey.core.domain.run

import com.skymonkey.core.domain.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * represents a run done by the user
 */
data class Run(
    val id: String?, // null if new run is created. assigned by DB.
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val location: Location,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?
) {
    val avgSpeedKmh: Double
        get() = (distanceMeters / 1000.0) / duration.toDouble(DurationUnit.HOURS)

    val avgSpeedMph: Double
        get() = (distanceMeters / 1609.344) / duration.toDouble(DurationUnit.HOURS)

    val maxSpeedMph: Double
        get() = (maxSpeedKmh * 0.621371)

    val totalElevationFeet: Int
        get() = (totalElevationMeters * 3.28084).toInt()
}
