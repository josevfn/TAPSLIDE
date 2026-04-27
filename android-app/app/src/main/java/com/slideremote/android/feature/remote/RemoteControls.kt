package com.slideremote.android.feature.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slideremote.android.core.model.MouseAction
import com.slideremote.android.core.model.RemoteCommand
import com.slideremote.android.ui.components.SlideIconActionButton

@Composable
fun RemoteControls(
    state: RemoteControlState,
    onBack: () -> Unit,
    onCommand: (RemoteCommand) -> Unit,
    onMouseMove: (Int, Int) -> Unit,
    onMouseAction: (MouseAction) -> Unit,
    onMouseScroll: (Int) -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ConnectionStatusBar(
            state = state,
            onBack = onBack,
            onPauseTimer = onPauseTimer,
            onResetTimer = onResetTimer
        )

        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            if (maxWidth > maxHeight) {
                LandscapeControls(
                    onCommand = onCommand,
                    onMouseMove = onMouseMove,
                    onMouseAction = onMouseAction,
                    onMouseScroll = onMouseScroll
                )
            } else {
                PortraitControls(
                    onCommand = onCommand,
                    onMouseMove = onMouseMove,
                    onMouseAction = onMouseAction,
                    onMouseScroll = onMouseScroll
                )
            }
        }
    }
}

@Composable
private fun PortraitControls(
    onCommand: (RemoteCommand) -> Unit,
    onMouseMove: (Int, Int) -> Unit,
    onMouseAction: (MouseAction) -> Unit,
    onMouseScroll: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RemoteButton(
            label = "VOLTAR",
            command = RemoteCommand.PREVIOUS_SLIDE,
            onCommand = onCommand,
            modifier = Modifier.height(124.dp)
        )
        RemoteButton(
            label = "AVANCAR",
            command = RemoteCommand.NEXT_SLIDE,
            onCommand = onCommand,
            modifier = Modifier.height(154.dp),
            prominent = true
        )
        MousePad(
            onMouseMove = onMouseMove,
            onMouseAction = onMouseAction,
            onScroll = onMouseScroll,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 210.dp)
        )
        SecondaryActions(onCommand = onCommand)
    }
}

@Composable
private fun LandscapeControls(
    onCommand: (RemoteCommand) -> Unit,
    onMouseMove: (Int, Int) -> Unit,
    onMouseAction: (MouseAction) -> Unit,
    onMouseScroll: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1.25f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RemoteButton(
                    label = "VOLTAR",
                    command = RemoteCommand.PREVIOUS_SLIDE,
                    onCommand = onCommand,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 120.dp)
                )
                RemoteButton(
                    label = "AVANCAR",
                    command = RemoteCommand.NEXT_SLIDE,
                    onCommand = onCommand,
                    modifier = Modifier
                        .weight(1.25f)
                        .heightIn(min = 120.dp),
                    prominent = true
                )
            }
            SecondaryActions(onCommand = onCommand)
        }
        MousePad(
            onMouseMove = onMouseMove,
            onMouseAction = onMouseAction,
            onScroll = onMouseScroll,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SecondaryActions(onCommand: (RemoteCommand) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SlideIconActionButton(
            label = "Iniciar",
            icon = Icons.Default.PlayArrow,
            onClick = { onCommand(RemoteCommand.START_PRESENTATION) },
            modifier = Modifier.width(76.dp)
        )
        SlideIconActionButton(
            label = "Esc",
            icon = Icons.Default.Close,
            onClick = { onCommand(RemoteCommand.END_PRESENTATION) },
            modifier = Modifier.width(76.dp)
        )
        SlideIconActionButton(
            label = "Preto",
            icon = Icons.Default.DarkMode,
            onClick = { onCommand(RemoteCommand.BLACK_SCREEN) },
            modifier = Modifier.width(76.dp)
        )
        SlideIconActionButton(
            label = "Branco",
            icon = Icons.Default.LightMode,
            onClick = { onCommand(RemoteCommand.WHITE_SCREEN) },
            modifier = Modifier.width(76.dp)
        )
    }
}
