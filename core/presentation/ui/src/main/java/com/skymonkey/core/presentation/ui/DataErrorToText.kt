package com.skymonkey.core.presentation.ui

import com.skymonkey.core.domain.DataError

/**
 * Extension function which maps error types to their corresponding error messages.
 * Maps them as UiText type.
 */
fun DataError.asUiText(): UiText =
    when (this) {
        DataError.Local.DISK_FULL ->
            UiText.StringResource(
                R.string.error_disk_full
            )
        DataError.Network.REQUEST_TIMEOUT ->
            UiText.StringResource(
                R.string.error_request_timeout
            )
        DataError.Network.TOO_MANY_REQUESTS ->
            UiText.StringResource(
                R.string.error_too_many_requests
            )
        DataError.Network.NO_INTERNET ->
            UiText.StringResource(
                R.string.error_no_internet
            )
        DataError.Network.PAYLOAD_TOO_LARGE ->
            UiText.StringResource(
                R.string.error_payload_large
            )
        DataError.Network.SERVER_ERROR ->
            UiText.StringResource(
                R.string.error_server_error
            )
        DataError.Network.SERIALIZATION ->
            UiText.StringResource(
                R.string.error_serialization
            )
        else -> UiText.StringResource(R.string.error_unkown)
    }
