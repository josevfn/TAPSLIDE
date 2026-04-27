package com.slideremote.android.feature.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slideremote.android.core.model.ConnectionStatus
import com.slideremote.android.core.model.MouseAction
import com.slideremote.android.core.model.RemoteCommand
import com.slideremote.android.core.transport.RemoteSessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

open class RemoteViewModel(
    private val mode: RemoteScreenMode = RemoteScreenMode.Live
) : ViewModel() {
    private var timerJob: Job? = null

    private val mutableState = MutableStateFlow(
        RemoteControlState(
            connectionName = if (mode == RemoteScreenMode.Demo) "Modo demonstracao" else "Notebook",
            transportLabel = if (mode == RemoteScreenMode.Demo) "Demo" else "Wi-Fi",
            connectionStatus = ConnectionStatus.Connected,
            logs = if (mode == RemoteScreenMode.Demo) listOf("Modo demonstracao iniciado.") else emptyList()
        )
    )
    val state: StateFlow<RemoteControlState> = mutableState

    init {
        if (mode == RemoteScreenMode.Live) {
            viewModelScope.launch {
                RemoteSessionManager.state.collect { session ->
                    mutableState.update {
                        it.copy(
                            connectionName = session.computerName,
                            connectionStatus = session.status,
                            transportLabel = session.host?.let { host -> "Wi-Fi $host" } ?: "Wi-Fi"
                        )
                    }
                }
            }
        }
    }

    fun onCommand(command: RemoteCommand) {
        if (command == RemoteCommand.START_PRESENTATION) {
            startTimer()
        }
        if (command == RemoteCommand.END_PRESENTATION) {
            pauseTimer()
        }

        val result = when (mode) {
            RemoteScreenMode.Live -> RemoteSessionManager.sendCommand(command)
            RemoteScreenMode.Demo -> Result.success(Unit)
        }

        val log = when {
            mode == RemoteScreenMode.Demo -> "Comando simulado: ${command.name}"
            result.isSuccess -> "Comando enviado: ${command.name}"
            else -> "Falha ao enviar ${command.name}: ${result.exceptionOrNull()?.message}"
        }

        mutableState.update {
            it.copy(
                lastCommand = command.name,
                logs = (listOf(log) + it.logs).take(6)
            )
        }
    }

    fun onMouseMove(deltaX: Int, deltaY: Int) {
        if (mode == RemoteScreenMode.Live) {
            RemoteSessionManager.sendMouse(MouseAction.MOVE, deltaX = deltaX, deltaY = deltaY)
        }
    }

    fun onMouseAction(action: MouseAction) {
        val result = when (mode) {
            RemoteScreenMode.Live -> RemoteSessionManager.sendMouse(action)
            RemoteScreenMode.Demo -> Result.success(Unit)
        }
        val log = when {
            mode == RemoteScreenMode.Demo -> "Mouse simulado: ${action.name}"
            result.isSuccess -> "Mouse enviado: ${action.name}"
            else -> "Falha no mouse ${action.name}: ${result.exceptionOrNull()?.message}"
        }
        mutableState.update {
            it.copy(
                lastCommand = "MOUSE_${action.name}",
                logs = (listOf(log) + it.logs).take(6)
            )
        }
    }

    fun onMouseScroll(scrollY: Int) {
        val result = when (mode) {
            RemoteScreenMode.Live -> RemoteSessionManager.sendMouse(MouseAction.SCROLL, scrollY = scrollY)
            RemoteScreenMode.Demo -> Result.success(Unit)
        }
        val log = when {
            mode == RemoteScreenMode.Demo -> "Rolagem simulada: $scrollY"
            result.isSuccess -> "Rolagem enviada: $scrollY"
            else -> "Falha na rolagem: ${result.exceptionOrNull()?.message}"
        }
        mutableState.update {
            it.copy(
                lastCommand = "MOUSE_SCROLL",
                logs = (listOf(log) + it.logs).take(6)
            )
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        mutableState.update { it.copy(timerRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        mutableState.update { it.copy(timerRunning = false, elapsedSeconds = 0) }
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return
        mutableState.update { it.copy(timerRunning = true) }
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                mutableState.update { current ->
                    if (current.timerRunning) {
                        current.copy(elapsedSeconds = current.elapsedSeconds + 1)
                    } else {
                        current
                    }
                }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

enum class RemoteScreenMode {
    Live,
    Demo
}

data class RemoteControlState(
    val connectionName: String,
    val transportLabel: String,
    val connectionStatus: ConnectionStatus,
    val elapsedSeconds: Long = 0,
    val timerRunning: Boolean = false,
    val lastCommand: String = "-",
    val logs: List<String> = emptyList(),
    val keepScreenOn: Boolean = true,
    val vibrateOnTap: Boolean = true,
    val gesturesEnabled: Boolean = true
)
