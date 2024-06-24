package com.skymonkey.core.connectivity.data

import com.google.android.gms.wearable.Node
import com.skymonkey.core.connectivity.domain.DeviceNode

fun Node.toDeviceNode(): DeviceNode =
    DeviceNode(
        id = id,
        displayName = displayName,
        isNearby = isNearby
    )
