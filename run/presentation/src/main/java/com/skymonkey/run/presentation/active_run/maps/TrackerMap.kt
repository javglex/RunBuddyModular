package com.skymonkey.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.awaitSnapshot
import com.skymonkey.core.domain.location.Location
import com.skymonkey.core.domain.location.LocationTimestamp
import com.skymonkey.core.presentation.designsystem.RunIcon
import com.skymonkey.run.presentation.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Custom Google maps composable
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TrackerMap(
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: List<List<LocationTimestamp>>,
    onSnapshot: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapStyle =
        remember {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
        }
    val cameraPositionState = rememberCameraPositionState() // zoom level, position etc.
    val markerState = rememberMarkerState() // marks current location

    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.latitude?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = "" // TODO experiment with this value
    )

    val markerPositionLong by animateFloatAsState(
        targetValue = currentLocation?.longitude?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = "" // TODO experiment with this value
    )

    val markerPosition =
        remember(markerPositionLat, markerPositionLong) {
            LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())
        }

    LaunchedEffect(key1 = markerPosition, key2 = isRunFinished) {
        if (!isRunFinished) {
            markerState.position = markerPosition
        }
    }

    // track our user if the run is not finished
    LaunchedEffect(key1 = currentLocation, key2 = isRunFinished) {
        if (currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            )
        }
    }

    var triggerCapture by remember {
        mutableStateOf(false)
    }
    var createSnapshotJob: Job? =
        remember {
            // to make sure there is only one snapshot taken at a time
            null
        }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties =
            MapProperties(
                mapStyleOptions = mapStyle
            ),
        uiSettings =
            MapUiSettings(
                zoomControlsEnabled = false
            ),
        modifier =
            if (isRunFinished) { // get maps ready for screenshot
                modifier
                    .width(300.dp)
                    .aspectRatio(16 / 9f)
                    .alpha(0f)
                    .onSizeChanged {
                        if (it.width >= 300) {
                            triggerCapture = true
                        }
                    }
            } else {
                modifier
            }
    ) {
        RunbuddyPolylines(locations = locations)

        // like launch effect but map specific. taken when our run is finished
        MapEffect(locations, isRunFinished, triggerCapture, createSnapshotJob) { map ->
            if (isRunFinished && triggerCapture && createSnapshotJob == null) {
                triggerCapture = false
                val boundsBuilder = LatLngBounds.builder() // google maps will figure out how to arrange the zoom based on our locations
                locations.flatten().forEach { locationTimeStamp ->
                    boundsBuilder
                        .include(
                            LatLng(
                                locationTimeStamp.locationWithAltitude.location.latitude,
                                locationTimeStamp.locationWithAltitude.location.longitude
                            )
                        )
                }
                // map and zoom into our locations
                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        100 // amount of padding to add around camera
                    )
                )

                // listen to when our camera stops moving
                map.setOnCameraIdleListener {
                    createSnapshotJob?.cancel()
                    createSnapshotJob =
                        GlobalScope.launch {
                            // TODO: supposedly to outlive composable and still get our screenshot. review if necessary to keep or substitute.
                            delay(500L) // add a bit of delay to make sure map is sharp and focused, before taking screenshot
                            map.awaitSnapshot()?.let(onSnapshot)
                        }
                }
            }
        }

        if (!isRunFinished && currentLocation != null) {
            MarkerComposable(
                currentLocation,
                state = markerState
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
