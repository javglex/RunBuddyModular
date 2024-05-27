package com.skymonkey.core.presentation.ui

import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

/**
 * Formats text to different units
 * e.g When tracking distance, we can display it as miles or kilometers, depending on localization
 */

fun Duration.formatted(): String {
    val totalSeconds = inWholeSeconds
    val hours = String.format(Locale.ROOT, "%02d", totalSeconds / 3600)
    val minutes = String.format(Locale.ROOT,"%02d", (totalSeconds % 3600)/ 60) // modulus removes the hour portion from totalSeconds
    val seconds = String.format(Locale.ROOT,"%02d", (totalSeconds % 60))

    return "$hours:$minutes:$seconds"
}

fun Double.toFormattedKm(): String {
    return "${this.roundToDecimals(1)} km"
}

fun Duration.toFormattedPace(distanceKm: Double): String {
    if (this == Duration.ZERO || distanceKm <= 0.0)
        return "-"
    val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
    val avgPaceMinutes = secondsPerKm / 60
    val avgPaceSeconds = String.format(Locale.ROOT,"%02d", secondsPerKm % 60) // modulus removes minutes from secondsPerJm

    return "$avgPaceMinutes:$avgPaceSeconds / km"
}


// rounds decimal places by decimalCount. e.g:
// 5.687
// decimal count = 1
// returns 5.7
private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}