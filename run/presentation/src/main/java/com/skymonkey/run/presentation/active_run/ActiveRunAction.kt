package com.skymonkey.run.presentation.active_run

/**
 * Actions for Active Run screen.
 */
sealed interface ActiveRunAction {
    data object OnToggleRunClick : ActiveRunAction

    data object OnFinishRunClick : ActiveRunAction

    data object OnResumeRunClick : ActiveRunAction

    data object OnBackClick : ActiveRunAction

    data class SubmitLocationPermissionInfo(
        val acceptedLocationPermission: Boolean,
        val showLocationRationale: Boolean
    ) : ActiveRunAction

    data class SubmitNotificationPermissionInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationRationale: Boolean
    ) : ActiveRunAction

    data object DismissRationaleDialog : ActiveRunAction

    data class OnRunProcessed(
        val mapPictureBytes: ByteArray
    ) : ActiveRunAction
}
