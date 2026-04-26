package com.slideremote.desktop.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Protocol {
    const val APP_ID = "slide-remote"
    const val VERSION = 1
    const val DEFAULT_PORT = 8765
    const val WEBSOCKET_PATH = "/slide-remote"
}

@Serializable
data class PairingQrPayload(
    val app: String = Protocol.APP_ID,
    val version: Int = Protocol.VERSION,
    val mode: String = "wifi",
    val host: String,
    val port: Int,
    val pairingCode: String
)

@Serializable
sealed class ServerMessage {
    abstract val type: String
    abstract val version: Int
}

@Serializable
data class PairingAcceptedMessage(
    override val type: String = "pairing_accepted",
    override val version: Int = Protocol.VERSION,
    val sessionId: String,
    val computerName: String
) : ServerMessage()

@Serializable
data class PairingRejectedMessage(
    override val type: String = "pairing_rejected",
    override val version: Int = Protocol.VERSION,
    val reason: String
) : ServerMessage()

@Serializable
data class CommandAckMessage(
    override val type: String = "command_ack",
    override val version: Int = Protocol.VERSION,
    val command: String,
    val timestamp: Long
) : ServerMessage()

@Serializable
data class ErrorMessage(
    override val type: String = "error",
    override val version: Int = Protocol.VERSION,
    val reason: String
) : ServerMessage()

@Serializable
data class PongMessage(
    override val type: String = "pong",
    override val version: Int = Protocol.VERSION,
    val timestamp: Long
) : ServerMessage()

@Serializable
data class IncomingEnvelope(
    val type: String,
    val version: Int = Protocol.VERSION,
    val sessionId: String? = null,
    val deviceName: String? = null,
    val pairingCode: String? = null,
    val command: String? = null,
    val timestamp: Long? = null
)

enum class RemoteCommand {
    NEXT_SLIDE,
    PREVIOUS_SLIDE,
    START_PRESENTATION,
    END_PRESENTATION,
    BLACK_SCREEN,
    WHITE_SCREEN,
    HEARTBEAT,
    PING,
    PONG
}

