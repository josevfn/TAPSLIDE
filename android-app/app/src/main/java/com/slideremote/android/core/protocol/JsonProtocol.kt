package com.slideremote.android.core.protocol

import com.slideremote.android.core.model.RemoteCommand
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

