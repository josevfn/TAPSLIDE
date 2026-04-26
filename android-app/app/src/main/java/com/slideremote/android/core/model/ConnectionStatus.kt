package com.slideremote.android.core.model

enum class ConnectionStatus(val label: String) {
    Disconnected("Desconectado"),
    Scanning("Escaneando"),
    Pairing("Pareando"),
    Connecting("Conectando"),
    Connected("Conectado"),
    Reconnecting("Reconectando"),
    Error("Erro")
}

