package com.skymonkey.run.location

import android.location.Location
import com.skymonkey.core.domain.location.LocationWithAltitude

fun Location.toLocationWithAltitude(): LocationWithAltitude =
    LocationWithAltitude(
        location =
            com.skymonkey.core.domain.location.Location(
                latitude = latitude,
                longitude = longitude
            ),
        altitude = altitude
    )
