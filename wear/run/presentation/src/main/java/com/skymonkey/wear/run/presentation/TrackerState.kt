package com.skymonkey.wear.run.presentation

import kotlin.time.Duration

data class TrackerState(
    val elapsedDuration: Duration = Duration.ZERO,
    val distanceMeters: Int = 0,
    val heartRate: Int = 0,
    val calories: Int = 0,
    val isTrackable: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val isConnectedPhoneNearby: Boolean = false,
    val isRunActive: Boolean = false,
    val canTrackHeartRate: Boolean = false,
    // ambient mode saves power, disables user touch, updates screen much less frequently
    val isAmbientMode: Boolean = false,
    // burn in protection for displays that may be susceptible to ghost pixels
    val burnInProtectionRequired: Boolean = false,
)
