package com.skymonkey.wear.run.data

import android.app.Application
import com.skymonkey.core.connectivity.domain.DeviceNode
import com.skymonkey.core.connectivity.domain.DeviceType
import com.skymonkey.core.connectivity.domain.NodeDiscovery
import com.skymonkey.core.connectivity.domain.messaging.MessagingAction
import com.skymonkey.core.connectivity.domain.messaging.MessagingClient
import com.skymonkey.core.connectivity.domain.messaging.MessagingError
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.wear.run.domain.PhoneConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)

class WatchToPhoneConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient
): PhoneConnector {

    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedNode: StateFlow<DeviceNode?>
        get() = _connectedNode.asStateFlow()

    override val messagingActions = nodeDiscovery
        .observeConnectedDevices(DeviceType.WATCH) // listen to devices from perspective of watch
        .flatMapLatest { connectedNodes ->
            val node = connectedNodes.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedNode.value = node
                messagingClient.connectToNode(node.id)
            } else flowOf()
        }
        .shareIn(
            applicationScope,
            SharingStarted.Eagerly
        )

    override suspend fun sendActionToPhone(action: MessagingAction): EmptyResult<MessagingError> {
        return messagingClient.sendOrQueueAction(action)
    }
}