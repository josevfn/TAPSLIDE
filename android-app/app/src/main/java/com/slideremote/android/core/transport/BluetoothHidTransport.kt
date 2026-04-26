package com.slideremote.android.core.transport

import com.slideremote.android.core.model.ConnectionStatus
import com.slideremote.android.core.model.RemoteCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothHidTransport : RemoteTransport {
    private val mutableStatus = MutableStateFlow(ConnectionStatus.Disconnected)
    override val status: StateFlow<ConnectionStatus> = mutableStatus

    override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Bluetooth HID depende de suporte do aparelho."))
    }

    override fun disconnect() {
        mutableStatus.value = ConnectionStatus.Disconnected
    }
}

