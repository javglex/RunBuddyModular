package com.skymonkey.run.presentation.active_run.model

import com.skymonkey.core.presentation.ui.toFormattedKm
import com.skymonkey.core.presentation.ui.toFormattedMi
import com.skymonkey.core.presentation.ui.toFormattedPaceImperial
import com.skymonkey.core.presentation.ui.toFormattedPaceMetric
import com.skymonkey.run.domain.RunData
import kotlin.time.Duration

fun RunData.toRunDataUi(isMetric: Boolean, elapsedTime: Duration): RunDataUi {
    val distanceKm = distanceMeters / 1000.0
    val distanceMi = distanceMeters / 1609.344

    return RunDataUi(
        distanceMeters = distanceMeters,
        distance = if (isMetric) distanceKm.toFormattedKm() else distanceMi.toFormattedMi(),
        pace = if (isMetric) elapsedTime.toFormattedPaceMetric(distanceKm) else elapsedTime.toFormattedPaceImperial(distanceMi),
        locations = locations,
        heartRates = heartRates
    )
}
