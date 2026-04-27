package com.slideremote.android.core.transport

import android.os.Build
import com.slideremote.android.core.model.ConnectionStatus
import com.slideremote.android.core.model.MouseAction
import com.slideremote.android.core.model.RemoteCommand
import com.slideremote.android.core.protocol.CommandMessage
import com.slideremote.android.core.protocol.JsonProtocol
import com.slideremote.android.core.protocol.PairingQrPayload
import com.slideremote.android.core.protocol.PairingRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

object RemoteSessionManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .build()

    private val mutableState = MutableStateFlow(RemoteSessionState())
    val state: StateFlow<RemoteSessionState> = mutableState

    private var webSocket: WebSocket? = null
    private var sessionId: String? = null
    private var pendingPairing: CompletableDeferred<Result<Unit>>? = null

    suspend fun connect(payload: PairingQrPayload): Result<Unit> {
        return connect(
            host = payload.host,
            port = payload.port,
            pairingCode = payload.pairingCode
        )
    }

    suspend fun connect(
        host: String,
        port: Int,
        pairingCode: String,
        deviceName: String = defaultDeviceName()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        disconnect()
        mutableState.value = RemoteSessionState(
            status = ConnectionStatus.Connecting,
            host = host,
            port = port
        )

        val deferred = CompletableDeferred<Result<Unit>>()
        pendingPairing = deferred
        val request = Request.Builder()
            .url("ws://$host:$port/slide-remote")
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    mutableState.update { it.copy(status = ConnectionStatus.Pairing) }
                    val message = JsonProtocol.pairingRequestToJson(
                        PairingRequest(
                            deviceName = deviceName,
                            pairingCode = pairingCode
                        )
                    )
                    webSocket.send(message)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    handleMessage(text)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    val result = Result.failure<Unit>(t)
                    pendingPairing?.complete(result)
                    pendingPairing = null
                    mutableState.update {
                        it.copy(
                            status = ConnectionStatus.Error,
                            errorMessage = t.message ?: "Falha na conexao."
                        )
                    }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    sessionId = null
                    mutableState.update {
                        if (it.status == ConnectionStatus.Connected) {
                            it.copy(status = ConnectionStatus.Disconnected)
                        } else {
                            it
                        }
                    }
                }
            }
        )

        val result = withTimeoutOrNull(10_000) { deferred.await() }
            ?: Result.failure(IllegalStateException("Tempo esgotado ao conectar."))

        if (result.isFailure) {
            webSocket?.close(1001, "pairing failed")
            pendingPairing = null
            mutableState.update {
                it.copy(
                    status = ConnectionStatus.Error,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }

        result
    }

    fun sendCommand(command: RemoteCommand): Result<Unit> {
        val id = sessionId ?: return Result.failure(IllegalStateException("Sessao nao conectada."))
        val socket = webSocket ?: return Result.failure(IllegalStateException("WebSocket nao conectado."))
        val sent = socket.send(JsonProtocol.commandToJson(CommandMessage(id, command)))
        return if (sent) Result.success(Unit) else Result.failure(IllegalStateException("Falha ao enviar comando."))
    }

    fun sendMouse(
        action: MouseAction,
        deltaX: Int = 0,
        deltaY: Int = 0,
        scrollY: Int = 0
    ): Result<Unit> {
        val id = sessionId ?: return Result.failure(IllegalStateException("Sessao nao conectada."))
        val socket = webSocket ?: return Result.failure(IllegalStateException("WebSocket nao conectado."))
        val sent = socket.send(
            JsonProtocol.mouseToJson(
                sessionId = id,
                action = action,
                deltaX = deltaX,
                deltaY = deltaY,
                scrollY = scrollY
            )
        )
        return if (sent) Result.success(Unit) else Result.failure(IllegalStateException("Falha ao enviar mouse."))
    }

    fun disconnect() {
        webSocket?.close(1000, "disconnect")
        webSocket = null
        sessionId = null
        pendingPairing = null
        mutableState.value = RemoteSessionState()
    }

    private fun handleMessage(text: String) {
        val pairing = JsonProtocol.parsePairingResponse(text).getOrNull()
        if (pairing != null && pendingPairing != null) {
            if (pairing.accepted && !pairing.sessionId.isNullOrBlank()) {
                sessionId = pairing.sessionId
                mutableState.update {
                    it.copy(
                        status = ConnectionStatus.Connected,
                        computerName = pairing.computerName ?: "Notebook",
                        errorMessage = null
                    )
                }
                pendingPairing?.complete(Result.success(Unit))
            } else {
                val reason = pairing.reason ?: "Pareamento recusado."
                mutableState.update {
                    it.copy(status = ConnectionStatus.Error, errorMessage = reason)
                }
                pendingPairing?.complete(Result.failure(IllegalStateException(reason)))
            }
            pendingPairing = null
        }
    }

    private fun defaultDeviceName(): String {
        val model = Build.MODEL?.takeIf { it.isNotBlank() } ?: "Android"
        return "Celular $model"
    }
}

data class RemoteSessionState(
    val status: ConnectionStatus = ConnectionStatus.Disconnected,
    val computerName: String = "Notebook",
    val host: String? = null,
    val port: Int? = null,
    val errorMessage: String? = null
)
