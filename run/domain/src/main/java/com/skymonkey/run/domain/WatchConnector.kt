package com.skymonkey.run.domain

import com.skymonkey.core.connectivity.domain.DeviceNode
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>

    fun setIsTrackable(isTrackable: Boolean)
}