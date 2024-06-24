package com.skymonkey.wear.run.presentation

import com.skymonkey.core.presentation.ui.UiText

sealed interface TrackerEvent {
    data object RunFinished : TrackerEvent

    data class Error(
        val message: UiText,
    ) : TrackerEvent
}
