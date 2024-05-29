package com.skymonkey.run.presentation.run_overview.mapper

import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.presentation.ui.formatted
import com.skymonkey.core.presentation.ui.toFormattedKm
import com.skymonkey.core.presentation.ui.toFormattedKmh
import com.skymonkey.core.presentation.ui.toFormattedMeters
import com.skymonkey.core.presentation.ui.toFormattedPace
import com.skymonkey.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUI(): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id ?: throw IllegalArgumentException(),
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl

    )
}