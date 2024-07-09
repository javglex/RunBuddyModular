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
    val minutes = String.format(Locale.ROOT, "%02d", (totalSeconds % 3600) / 60) // modulus removes the hour portion from totalSeconds
    val seconds = String.format(Locale.ROOT, "%02d", (totalSeconds % 60))

    return "$hours:$minutes:$seconds"
}

fun Double.toFormattedKm(): String = "${this.roundToDecimals(1)} km"
fun Double.toFormattedMi(): String = "${this.roundToDecimals(1)} mi"
fun Double.toFormattedKmh(): String = "${this.roundToDecimals(1)} km/h"
fun Double.toFormattedMph(): String = "${this.roundToDecimals(1)} mph"

fun Int.toFormattedMeters(): String = "$this m"
fun Int.toFormattedFeet(): String = "$this ft"
fun Double.metersToKm(): String = "${(this/1000.0).roundToDecimals(1)} km"

fun Duration.toFormattedPaceMetric(distanceKm: Double): String {
    if (this == Duration.ZERO || distanceKm <= 0.0) {
        return "-"
    }
    val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
    val avgPaceMinutes = secondsPerKm / 60
    val avgPaceSeconds = String.format(Locale.ROOT, "%02d", secondsPerKm % 60) // modulus removes minutes from secondsPerJm

    return "$avgPaceMinutes:$avgPaceSeconds / km"
}

fun Duration.toFormattedPaceImperial(distanceMi: Double): String {
    if (this == Duration.ZERO || distanceMi <= 0.0) {
        return "-"
    }
    val secondsPerMi = (this.inWholeSeconds / distanceMi).roundToInt()
    val avgPaceMinutes = secondsPerMi / 60
    val avgPaceSeconds = String.format(Locale.ROOT, "%02d", secondsPerMi % 60) // modulus removes minutes from secondsPerJm

    return "$avgPaceMinutes:$avgPaceSeconds / mi"
}

// rounds decimal places by decimalCount. e.g:
// 5.687
// decimal count = 1
// returns 5.7
private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}

fun Int?.toFormattedHeartRate(): String = if (this != null) "$this bpm" else "-"

fun Int?.toFormattedCalories(): String = if (this != null) "$this Cal" else "-"
