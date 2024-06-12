package com.skymonkey.wear.run.presentation

import androidx.wear.ambient.AmbientLifecycleObserver

sealed interface TrackerAction {
    data object OnToggleRunClick : TrackerAction
    data object OnFinishRunClick : TrackerAction
    data class OnBodySensorPermissionResult(val isGranted: Boolean) : TrackerAction
    data class OnEnterAmbientMode(val burnInProtectionRequired: Boolean) : TrackerAction
    data object OnExitAmbientMode: TrackerAction
}