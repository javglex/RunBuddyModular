package com.skymonkey.core.connectivity.domain

import kotlinx.coroutines.flow.Flow

/**
 * Allows us to observe connected notes. e.g phone/smartwatch
 */
interface NodeDiscovery {
    /**
     * Observe connected devices from the perspective of [localDeviceType]
     */
    fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>>
}
