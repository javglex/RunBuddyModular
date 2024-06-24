package com.skymonkey.run.network

import com.skymonkey.core.domain.location.Location
import com.skymonkey.core.domain.run.Run
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run =
    Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(lat, long),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )

/**
 * We apply the not-null assertion operator on the id because we MUST have an id when creating our DTO.
 * If we don't have our ID, we want an exception.
 */
fun Run.toCreateRunRequest(): CreateRunRequest =
    CreateRunRequest(
        id = id!!,
        epochMillis = dateTimeUtc.toEpochSecond() * 1000,
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        lat = location.latitude,
        long = location.longitude,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )
