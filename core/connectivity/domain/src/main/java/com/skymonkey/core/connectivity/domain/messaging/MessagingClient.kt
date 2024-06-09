package com.skymonkey.core.connectivity.domain.messaging

import com.skymonkey.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow

interface MessagingClient {
    fun connectToNode(nodeId: String): Flow<MessagingAction>
    suspend fun sendOrQueueAction(action: MessagingAction): EmptyResult<MessagingError>
}