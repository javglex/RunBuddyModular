package com.skymonkey.run.presentation.active_run

import com.skymonkey.core.presentation.ui.UiText

sealed interface ActiveRunEvent {
    data class Error(
        val error: UiText
    ) : ActiveRunEvent

    data object RunSaved : ActiveRunEvent
}
