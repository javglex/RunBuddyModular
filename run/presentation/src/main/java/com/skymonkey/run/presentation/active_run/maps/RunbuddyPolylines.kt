package com.skymonkey.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import com.skymonkey.core.domain.location.LocationTimestamp

/**
 * draws lines between locations in our tracker map
 */
@Composable
fun RunbuddyPolylines(
    locations: List<List<LocationTimestamp>>,
    modifier: Modifier = Modifier
) {
   val polylines = remember(locations) {
       locations.map {
           it.zipWithNext { locationTimestampA, locationTimestampB ->
               PolyLineUi(
                   locationA = locationTimestampA.locationWithAltitude.location,
                   locationB = locationTimestampB.locationWithAltitude.location,
                   color = PolylineColorCalculator.locationsToColor(
                       locationA = locationTimestampA,
                       locationB = locationTimestampB
                   )
               )
           }
       }
   }

    polylines.forEach { polyline ->
        polyline.forEach { polyUi ->
            Polyline(
                points = listOf(
                    LatLng(polyUi.locationA.latitude, polyUi.locationA.longitude),
                    LatLng(polyUi.locationB.latitude, polyUi.locationB.longitude)
                ),
                color = polyUi.color,
                jointType = JointType.BEVEL // defines what joint looks like when going around a corner
            )
        }
    }
}