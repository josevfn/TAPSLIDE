package com.slideremote.android.feature.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slideremote.android.core.protocol.ProtocolConstants
import com.slideremote.android.core.protocol.PairingQrPayload
import com.slideremote.android.core.transport.RemoteSessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ConnectionViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(ConnectionUiState())
    val state: StateFlow<ConnectionUiState> = mutableState

    fun updateHost(value: String) {
        mutableState.update { it.copy(host = value.trim()) }
    }

    fun updatePort(value: String) {
        mutableState.update { it.copy(port = value.filter(Char::isDigit).take(5)) }
    }

    fun updatePairingCode(value: String) {
        mutableState.update { it.copy(pairingCode = value.filter(Char::isDigit).take(6)) }
    }

    fun toggleHotspotInstructions() {
        mutableState.update { it.copy(showHotspotInstructions = !it.showHotspotInstructions) }
    }

    fun connectManual(onConnected: () -> Unit) {
        val current = mutableState.value
        val port = current.port.toIntOrNull() ?: return
        connect(
            host = current.host,
            port = port,
            pairingCode = current.pairingCode,
            onConnected = onConnected
        )
    }

    fun connectQr(payload: PairingQrPayload, onConnected: () -> Unit) {
        connect(
            host = payload.host,
            port = payload.port,
            pairingCode = payload.pairingCode,
            onConnected = onConnected
        )
    }

    private fun connect(
        host: String,
        port: Int,
        pairingCode: String,
        onConnected: () -> Unit
    ) {
        viewModelScope.launch {
            mutableState.update { it.copy(connecting = true, errorMessage = null) }
            val result = RemoteSessionManager.connect(
                host = host,
                port = port,
                pairingCode = pairingCode
            )
            mutableState.update {
                it.copy(
                    connecting = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
            if (result.isSuccess) {
                onConnected()
            }
        }
    }
}

data class ConnectionUiState(
    val host: String = "",
    val port: String = ProtocolConstants.DEFAULT_PORT.toString(),
    val pairingCode: String = "",
    val showHotspotInstructions: Boolean = false,
    val connecting: Boolean = false,
    val errorMessage: String? = null
) {
    val canConnect: Boolean
        get() = host.isNotBlank() && port.toIntOrNull() != null && pairingCode.length == 6
}
