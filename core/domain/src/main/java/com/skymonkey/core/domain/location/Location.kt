package com.skymonkey.core.domain.location

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Location(
    val latitude: Double,
    val longitude: Double,
) {
    /**
     * calculates distance between two locations (this & parameter)
     * @param other - the other location to compare distance to
     * @return the distance between two points in meters, taking into account earth radius.
     */
    fun distanceTo(other: Location): Float {
        val latDistance = Math.toRadians(other.latitude - latitude)
        val longDistance = Math.toRadians(other.longitude - longitude)
        val a =
            sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(longDistance / 2) * sin(longDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c.toFloat()
    }

    companion object {
        private const val EARTH_RADIUS_METERS = 6_371_000
    }
}
