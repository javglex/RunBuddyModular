package com.skymonkey.auth.presentation.login

import com.skymonkey.core.presentation.ui.UiText

sealed interface LoginEvent {
    data class Error(
        val error: UiText
    ) : LoginEvent

    data object LoginSuccess : LoginEvent
}
