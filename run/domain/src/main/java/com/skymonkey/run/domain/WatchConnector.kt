package com.skymonkey.run.domain

import com.skymonkey.core.connectivity.domain.DeviceNode
import com.skymonkey.core.connectivity.domain.messaging.MessagingAction
import com.skymonkey.core.connectivity.domain.messaging.MessagingError
import com.skymonkey.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError>

    fun setIsTrackable(isTrackable: Boolean)
}
