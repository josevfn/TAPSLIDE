package com.slideremote.android.core.model

enum class RemoteCommand(val displayName: String) {
    NEXT_SLIDE("Avancar"),
    PREVIOUS_SLIDE("Voltar"),
    START_PRESENTATION("Iniciar"),
    END_PRESENTATION("Esc"),
    BLACK_SCREEN("Preto"),
    WHITE_SCREEN("Branco"),
    HEARTBEAT("Heartbeat"),
    PING("Ping"),
    PONG("Pong")
}

enum class MouseAction {
    MOVE,
    LEFT_CLICK,
    RIGHT_CLICK,
    DOUBLE_CLICK,
    SCROLL
}

