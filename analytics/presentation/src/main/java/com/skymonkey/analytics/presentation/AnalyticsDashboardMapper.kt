package com.skymonkey.analytics.presentation

import com.skymonkey.analytics.domain.AnalyticsValues
import com.skymonkey.core.presentation.ui.formatted
import com.skymonkey.core.presentation.ui.toFormattedKm
import com.skymonkey.core.presentation.ui.toFormattedKmh
import com.skymonkey.core.presentation.ui.toFormattedMeters
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24 // modulus 24 removes days
    val minutes = toLong(DurationUnit.MINUTES) % 60 // modulus 60 remove hours

    return "$days d $hours h $minutes min"
}
fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestRun = fastestEverRun.toFormattedKmh(),
        avgDistance = (avgDistanceRun / 1000.0).toFormattedKm(),
        avgPace = avgPacePerRun.seconds.formatted()
    )
}