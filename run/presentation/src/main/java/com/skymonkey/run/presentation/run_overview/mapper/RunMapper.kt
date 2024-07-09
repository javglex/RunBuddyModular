package com.skymonkey.run.presentation.run_overview.mapper

import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.presentation.ui.formatted
import com.skymonkey.core.presentation.ui.toFormattedFeet
import com.skymonkey.core.presentation.ui.toFormattedHeartRate
import com.skymonkey.core.presentation.ui.toFormattedKm
import com.skymonkey.core.presentation.ui.toFormattedKmh
import com.skymonkey.core.presentation.ui.toFormattedMeters
import com.skymonkey.core.presentation.ui.toFormattedMi
import com.skymonkey.core.presentation.ui.toFormattedMph
import com.skymonkey.core.presentation.ui.toFormattedPaceImperial
import com.skymonkey.core.presentation.ui.toFormattedPaceMetric
import com.skymonkey.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUI(isMetric: Boolean = true): RunUi {
    val dateTimeInLocalTime =
        dateTimeUtc
            .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime =
        DateTimeFormatter
            .ofPattern("MMM dd, yyyy - hh:mma")
            .format(dateTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0
    val distanceMi = distanceMeters / 1609.344

    return RunUi(
        id = id ?: throw IllegalArgumentException(),
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = if (isMetric) distanceKm.toFormattedKm() else distanceMi.toFormattedMi(),
        avgSpeed = if (isMetric) avgSpeedKmh.toFormattedKmh() else avgSpeedMph.toFormattedMph(),
        maxSpeed = if(isMetric) maxSpeedKmh.toFormattedKmh() else maxSpeedMph.toFormattedMph(),
        pace = if (isMetric) duration.toFormattedPaceMetric(distanceKm) else duration.toFormattedPaceImperial(distanceMi),
        totalElevation = if (isMetric) totalElevationMeters.toFormattedMeters() else totalElevationFeet.toFormattedFeet(),
        mapPictureUrl = mapPictureUrl,
        avgHeartRate = avgHeartRate.toFormattedHeartRate(),
        maxHeartRate = maxHeartRate.toFormattedHeartRate()
    )
}
