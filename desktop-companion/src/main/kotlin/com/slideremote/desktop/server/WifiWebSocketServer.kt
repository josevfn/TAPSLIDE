package com.slideremote.desktop.server

import com.slideremote.desktop.input.KeyboardController
import com.slideremote.desktop.input.MouseController
import com.slideremote.desktop.protocol.CommandAckMessage
import com.slideremote.desktop.protocol.ErrorMessage
import com.slideremote.desktop.protocol.IncomingEnvelope
import com.slideremote.desktop.protocol.MouseAckMessage
import com.slideremote.desktop.protocol.MouseAction
import com.slideremote.desktop.protocol.PongMessage
import com.slideremote.desktop.protocol.Protocol
import com.slideremote.desktop.protocol.RemoteCommand
import com.slideremote.desktop.security.SessionTokenManager
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WifiWebSocketServer(
    private val port: Int = Protocol.DEFAULT_PORT,
    private val pairingServer: PairingServer,
    private val sessionTokenManager: SessionTokenManager,
    private val keyboardController: KeyboardController,
    private val mouseController: MouseController,
    private val listener: Listener
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private var engine: ApplicationEngine? = null

    fun start() {
        if (engine != null) return

        engine = embeddedServer(CIO, host = "0.0.0.0", port = port) {
            install(WebSockets) {
                pingPeriodMillis = 20_000
                timeoutMillis = 30_000
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            routing {
                webSocket(Protocol.WEBSOCKET_PATH) {
                    listener.onStatusChanged("aguardando conexao")
                    try {
                        for (frame in incoming) {
                            if (frame !is Frame.Text) continue
                            val text = frame.readText()
                            val response = handleMessage(text)
                            outgoing.send(Frame.Text(response))
                        }
                    } catch (_: ClosedReceiveChannelException) {
                        listener.onLog("Cliente desconectado.")
                    } catch (exception: Exception) {
                        listener.onLog("Erro no WebSocket: ${exception.message}")
                    }
                }
            }
        }.start(wait = false)

        listener.onStatusChanged("aguardando conexao")
        listener.onLog("Servidor iniciado na porta $port.")
    }

    fun stop() {
        engine?.stop(gracePeriodMillis = 500, timeoutMillis = 1_500)
        engine = null
        sessionTokenManager.disconnect()
        listener.onDisconnected()
        listener.onStatusChanged("parado")
        listener.onLog("Servidor parado.")
    }

    fun isRunning(): Boolean = engine != null

    private fun handleMessage(rawText: String): String {
        val envelope = try {
            json.decodeFromString<IncomingEnvelope>(rawText)
        } catch (exception: SerializationException) {
            listener.onLog("JSON invalido recebido.")
            return json.encodeToString(ErrorMessage(reason = "INVALID_JSON"))
        }

        if (envelope.version != Protocol.VERSION) {
            return json.encodeToString(ErrorMessage(reason = "UNSUPPORTED_VERSION"))
        }

        return when (envelope.type) {
            "pairing_request" -> handlePairing(envelope)
            "command" -> handleCommand(envelope)
            "mouse" -> handleMouse(envelope)
            "heartbeat", "ping" -> json.encodeToString(PongMessage(timestamp = System.currentTimeMillis()))
            else -> json.encodeToString(ErrorMessage(reason = "UNKNOWN_TYPE"))
        }
    }

    private fun handlePairing(envelope: IncomingEnvelope): String {
        return when (val result = pairingServer.pair(envelope.deviceName, envelope.pairingCode)) {
            is PairingResult.Accepted -> {
                listener.onConnected(envelope.deviceName ?: "Celular")
                listener.onStatusChanged("conectado")
                listener.onLog("Pareamento aceito: ${envelope.deviceName}.")
                json.encodeToString(result.message)
            }
            is PairingResult.Rejected -> {
                listener.onLog("Pareamento recusado: ${result.message.reason}.")
                json.encodeToString(result.message)
            }
        }
    }

    private fun handleCommand(envelope: IncomingEnvelope): String {
        if (!sessionTokenManager.validate(envelope.sessionId)) {
            listener.onLog("Comando recusado: sessao invalida.")
            return json.encodeToString(ErrorMessage(reason = "INVALID_SESSION"))
        }

        val command = try {
            RemoteCommand.valueOf(envelope.command.orEmpty())
        } catch (_: IllegalArgumentException) {
            return json.encodeToString(ErrorMessage(reason = "UNKNOWN_COMMAND"))
        }

        if (command == RemoteCommand.HEARTBEAT || command == RemoteCommand.PING) {
            return json.encodeToString(PongMessage(timestamp = System.currentTimeMillis()))
        }

        val result = keyboardController.execute(command)
        return if (result.isSuccess) {
            val timestamp = System.currentTimeMillis()
            listener.onCommand(command.name, timestamp)
            json.encodeToString(CommandAckMessage(command = command.name, timestamp = timestamp))
        } else {
            listener.onLog("Falha ao executar ${command.name}: ${result.exceptionOrNull()?.message}")
            json.encodeToString(ErrorMessage(reason = "KEYBOARD_ERROR"))
        }
    }

    private fun handleMouse(envelope: IncomingEnvelope): String {
        if (!sessionTokenManager.validate(envelope.sessionId)) {
            listener.onLog("Mouse recusado: sessao invalida.")
            return json.encodeToString(ErrorMessage(reason = "INVALID_SESSION"))
        }

        val action = try {
            MouseAction.valueOf(envelope.action.orEmpty())
        } catch (_: IllegalArgumentException) {
            return json.encodeToString(ErrorMessage(reason = "UNKNOWN_MOUSE_ACTION"))
        }

        val result = mouseController.execute(
            action = action,
            deltaX = envelope.deltaX,
            deltaY = envelope.deltaY,
            scrollY = envelope.scrollY
        )

        return if (result.isSuccess) {
            val timestamp = System.currentTimeMillis()
            if (action != MouseAction.MOVE) {
                listener.onCommand("MOUSE_${action.name}", timestamp)
            }
            json.encodeToString(MouseAckMessage(action = action.name, timestamp = timestamp))
        } else {
            listener.onLog("Falha ao executar mouse ${action.name}: ${result.exceptionOrNull()?.message}")
            json.encodeToString(ErrorMessage(reason = "MOUSE_ERROR"))
        }
    }

    interface Listener {
        fun onStatusChanged(status: String)
        fun onConnected(deviceName: String)
        fun onDisconnected()
        fun onCommand(command: String, timestamp: Long)
        fun onLog(message: String)
    }
}
