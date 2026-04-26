package com.slideremote.android.feature.remote

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slideremote.android.core.model.RemoteCommand
import com.slideremote.android.ui.components.BigRoundButton

@Composable
fun RemoteButton(
    label: String,
    command: RemoteCommand,
    onCommand: (RemoteCommand) -> Unit,
    modifier: Modifier = Modifier,
    prominent: Boolean = false
) {
    BigRoundButton(
        label = label,
        onClick = { onCommand(command) },
        modifier = modifier,
        prominent = prominent
    )
}

