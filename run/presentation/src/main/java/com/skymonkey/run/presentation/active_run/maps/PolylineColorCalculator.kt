package com.skymonkey.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.skymonkey.core.domain.location.LocationTimestamp
import kotlin.math.abs

/**
 * calculates color of line between two locations. color is dependant on user run speed.
 */
object PolylineColorCalculator {
    fun locationsToColor(
        locationA: LocationTimestamp,
        locationB: LocationTimestamp,
    ): Color {
        val distanceMeters = locationA.locationWithAltitude.location.distanceTo(locationB.locationWithAltitude.location)
        val timeDiff = abs((locationB.durationTimeStamp - locationA.durationTimeStamp).inWholeSeconds)
        val speed = (distanceMeters / timeDiff) * 3.6

        return interpolateColor(
            speedKmh = speed,
            colorStart = Color.Green,
            colorMid = Color.Yellow,
            colorEnd = Color.Red
        )
    }

    private fun interpolateColor(
        speedKmh: Double = 12.5,
        minSpeed: Double = 5.0,
        maxSpeed: Double = 20.0,
        colorStart: Color,
        colorMid: Color,
        colorEnd: Color,
    ): Color {
        val ratio = ((speedKmh - minSpeed) / (maxSpeed - minSpeed)).coerceIn(0.0..1.0)
        val colorInt =
            if (ratio <= 0.5) {
                val midRatio = ratio / 0.5 // normalize to get a ratio from 0 to 1
                ColorUtils.blendARGB(colorStart.toArgb(), colorMid.toArgb(), midRatio.toFloat())
            } else {
                val midToEndRatio = (ratio - 0.5) / 0.5
                ColorUtils.blendARGB(colorMid.toArgb(), colorEnd.toArgb(), midToEndRatio.toFloat())
            }

        return Color(colorInt)
    }
}
