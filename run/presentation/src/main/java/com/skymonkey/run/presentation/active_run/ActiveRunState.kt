package com.skymonkey.run.presentation.active_run

import com.skymonkey.core.domain.location.Location
import com.skymonkey.run.domain.RunData
import com.skymonkey.run.presentation.active_run.model.RunDataUi
import kotlin.time.Duration

data class ActiveRunState(
    val elapsedTime: Duration = Duration.ZERO,
    val runData: RunDataUi = RunDataUi(),
    val shouldTrack: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val currentLocation: Location? = null,
    var isRunFinished: Boolean = false,
    val isSaving: Boolean = false,
    val showLocationRationale: Boolean = false,
    val showNotificationRationale: Boolean = false,
    val showLoadingChunks: Boolean = false
)
