package com.slideremote.android.core.protocol

import com.slideremote.android.core.model.RemoteCommand
import com.slideremote.android.core.model.MouseAction
import org.json.JSONObject

object JsonProtocol {
    fun commandToJson(message: CommandMessage): String {
        return JSONObject()
            .put("type", ProtocolConstants.TYPE_COMMAND)
            .put("version", ProtocolConstants.VERSION)
            .put("sessionId", message.sessionId)
            .put("command", message.command.name)
            .put("timestamp", message.timestamp)
            .toString()
    }

    fun pairingRequestToJson(message: PairingRequest): String {
        return JSONObject()
            .put("type", ProtocolConstants.TYPE_PAIRING_REQUEST)
            .put("version", ProtocolConstants.VERSION)
            .put("deviceName", message.deviceName)
            .put("pairingCode", message.pairingCode)
            .toString()
    }

    fun heartbeatToJson(sessionId: String, timestamp: Long = System.currentTimeMillis()): String {
        return JSONObject()
            .put("type", ProtocolConstants.TYPE_HEARTBEAT)
            .put("version", ProtocolConstants.VERSION)
            .put("sessionId", sessionId)
            .put("timestamp", timestamp)
            .toString()
    }

    fun mouseToJson(
        sessionId: String,
        action: MouseAction,
        deltaX: Int = 0,
        deltaY: Int = 0,
        scrollY: Int = 0,
        timestamp: Long = System.currentTimeMillis()
    ): String {
        return JSONObject()
            .put("type", "mouse")
            .put("version", ProtocolConstants.VERSION)
            .put("sessionId", sessionId)
            .put("action", action.name)
            .put("deltaX", deltaX)
            .put("deltaY", deltaY)
            .put("scrollY", scrollY)
            .put("timestamp", timestamp)
            .toString()
    }

    fun parsePairingResponse(rawJson: String): Result<PairingResponse> {
        return runCatching {
            val json = JSONObject(rawJson)
            when (json.getString("type")) {
                ProtocolConstants.TYPE_PAIRING_ACCEPTED -> PairingResponse(
                    accepted = true,
                    sessionId = json.getString("sessionId"),
                    computerName = json.optString("computerName", "Notebook")
                )
                ProtocolConstants.TYPE_PAIRING_REJECTED -> PairingResponse(
                    accepted = false,
                    reason = json.optString("reason", "PAIRING_REJECTED")
                )
                "error" -> PairingResponse(
                    accepted = false,
                    reason = json.optString("reason", "ERROR")
                )
                else -> PairingResponse(
                    accepted = false,
                    reason = "UNEXPECTED_RESPONSE"
                )
            }
        }
    }

    fun parsePairingQr(rawJson: String): Result<PairingQrPayload> {
        return runCatching {
            val json = JSONObject(rawJson)
            PairingQrPayload(
                app = json.getString("app"),
                version = json.getInt("version"),
                mode = json.getString("mode"),
                host = json.getString("host"),
                port = json.getInt("port"),
                pairingCode = json.getString("pairingCode")
            )
        }.mapCatching { payload ->
            require(payload.app == ProtocolConstants.APP_ID) { "INVALID_APP" }
            require(payload.version == ProtocolConstants.VERSION) { "UNSUPPORTED_VERSION" }
            payload
        }
    }

    fun parseCommand(rawJson: String): Result<CommandMessage> {
        return runCatching {
            val json = JSONObject(rawJson)
            require(json.getString("type") == ProtocolConstants.TYPE_COMMAND) { "INVALID_TYPE" }
            CommandMessage(
                sessionId = json.getString("sessionId"),
                command = RemoteCommand.valueOf(json.getString("command")),
                timestamp = json.getLong("timestamp")
            )
        }
    }
}
