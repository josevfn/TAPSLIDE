package com.slideremote.android.feature.remote

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slideremote.android.core.model.RemoteCommand
import com.slideremote.android.core.wakelock.KeepScreenOnController
import com.slideremote.android.ui.components.PrimaryActionButton
import kotlin.math.abs

@Composable
fun RemoteScreen(
    onBack: () -> Unit,
    viewModel: RemoteViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RemoteScreenContent(
        state = state,
        onBack = onBack,
        onCommand = viewModel::onCommand,
        onPauseTimer = viewModel::pauseTimer,
        onResetTimer = viewModel::resetTimer
    )
}

@Composable
fun RemoteScreenContent(
    state: RemoteControlState,
    onBack: () -> Unit,
    onCommand: (RemoteCommand) -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    KeepScreenOnController(enabled = state.keepScreenOn)
    val haptic = LocalHapticFeedback.current
    var showQuickActions by remember { mutableStateOf(false) }

    fun send(command: RemoteCommand) {
        if (state.vibrateOnTap) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        onCommand(command)
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
            .remoteGestures(
                enabled = state.gesturesEnabled,
                onCommand = ::send,
                onLongPress = { showQuickActions = true }
            )
    ) {
        RemoteControls(
            state = state,
            onBack = onBack,
            onCommand = ::send,
            onPauseTimer = onPauseTimer,
            onResetTimer = onResetTimer
        )
    }

    if (showQuickActions) {
        AlertDialog(
            onDismissRequest = { showQuickActions = false },
            title = { Text("Acoes rapidas") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ultimos comandos",
                        fontWeight = FontWeight.SemiBold
                    )
                    state.logs.ifEmpty { listOf("Nenhum comando enviado.") }.forEach {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    PrimaryActionButton(
                        text = "Tela preta",
                        onClick = {
                            send(RemoteCommand.BLACK_SCREEN)
                            showQuickActions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    PrimaryActionButton(
                        text = "Tela branca",
                        onClick = {
                            send(RemoteCommand.WHITE_SCREEN)
                            showQuickActions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQuickActions = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}

private fun Modifier.remoteGestures(
    enabled: Boolean,
    onCommand: (RemoteCommand) -> Unit,
    onLongPress: () -> Unit
): Modifier {
    if (!enabled) return this
    return this
        .pointerInput(enabled) {
            detectTapGestures(
                onTap = { offset ->
                    if (offset.x > size.width / 2f) {
                        onCommand(RemoteCommand.NEXT_SLIDE)
                    } else {
                        onCommand(RemoteCommand.PREVIOUS_SLIDE)
                    }
                },
                onLongPress = { onLongPress() }
            )
        }
        .pointerInput(enabled) {
            var dragAmount by mutableFloatStateOf(0f)
            detectHorizontalDragGestures(
                onDragStart = { dragAmount = 0f },
                onHorizontalDrag = { _, amount -> dragAmount += amount },
                onDragEnd = {
                    if (abs(dragAmount) > 80f) {
                        if (dragAmount < 0f) {
                            onCommand(RemoteCommand.NEXT_SLIDE)
                        } else {
                            onCommand(RemoteCommand.PREVIOUS_SLIDE)
                        }
                    }
                }
            )
        }
}
