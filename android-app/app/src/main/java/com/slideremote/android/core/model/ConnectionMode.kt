package com.slideremote.android.core.model

enum class ConnectionMode(
    val title: String,
    val subtitle: String
) {
    BluetoothDirect(
        title = "Bluetooth direto",
        subtitle = "Sem instalar nada no computador"
    ),
    WifiHotspot(
        title = "Wi-Fi / Hotspot",
        subtitle = "Mais estavel para apresentacoes"
    ),
    Demo(
        title = "Modo demonstracao",
        subtitle = "Testar sem conectar"
    )
}

