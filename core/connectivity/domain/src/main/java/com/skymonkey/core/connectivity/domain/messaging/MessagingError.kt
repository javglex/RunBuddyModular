package com.skymonkey.core.connectivity.domain.messaging

import com.skymonkey.core.domain.Error

enum class MessagingError: Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}