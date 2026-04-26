package com.slideremote.android.feature.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slideremote.android.core.model.ConnectionStatus
import com.slideremote.android.core.model.RemoteCommand
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

    fun onCommand(command: RemoteCommand) {
        if (command == RemoteCommand.START_PRESENTATION) {
            startTimer()
        }
        if (command == RemoteCommand.END_PRESENTATION) {
            pauseTimer()
        }

        val log = when (mode) {
            RemoteScreenMode.Live -> "Comando preparado: ${command.name}"
            RemoteScreenMode.Demo -> "Comando simulado: ${command.name}"
        }

        mutableState.update {
            it.copy(
                lastCommand = command.name,
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

