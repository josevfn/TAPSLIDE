package com.slideremote.android.core.transport

import com.slideremote.android.core.model.ConnectionStatus
import com.slideremote.android.core.model.RemoteCommand
import kotlinx.coroutines.flow.StateFlow

interface RemoteTransport {
    val status: StateFlow<ConnectionStatus>
    suspend fun sendCommand(command: RemoteCommand): Result<Unit>
    fun disconnect()
}

