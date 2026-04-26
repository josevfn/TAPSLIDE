package com.slideremote.android.core.transport

import com.slideremote.android.core.model.ConnectionMode

object TransportFactory {
    fun create(mode: ConnectionMode): RemoteTransport {
        return when (mode) {
            ConnectionMode.BluetoothDirect -> BluetoothHidTransport()
            ConnectionMode.WifiHotspot -> WifiTransport()
            ConnectionMode.Demo -> DemoTransport()
        }
    }
}

class DemoTransport : RemoteTransport {
    private val mutableStatus = kotlinx.coroutines.flow.MutableStateFlow(com.slideremote.android.core.model.ConnectionStatus.Connected)
    override val status: kotlinx.coroutines.flow.StateFlow<com.slideremote.android.core.model.ConnectionStatus> = mutableStatus

    override suspend fun sendCommand(command: com.slideremote.android.core.model.RemoteCommand): Result<Unit> {
        return Result.success(Unit)
    }

    override fun disconnect() {
        mutableStatus.value = com.slideremote.android.core.model.ConnectionStatus.Disconnected
    }
}

