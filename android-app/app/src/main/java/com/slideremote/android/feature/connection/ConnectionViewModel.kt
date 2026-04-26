package com.slideremote.android.feature.connection

import androidx.lifecycle.ViewModel
import com.slideremote.android.core.protocol.ProtocolConstants
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
}

data class ConnectionUiState(
    val host: String = "",
    val port: String = ProtocolConstants.DEFAULT_PORT.toString(),
    val pairingCode: String = "",
    val showHotspotInstructions: Boolean = false
) {
    val canConnect: Boolean
        get() = host.isNotBlank() && port.toIntOrNull() != null && pairingCode.length == 6
}

