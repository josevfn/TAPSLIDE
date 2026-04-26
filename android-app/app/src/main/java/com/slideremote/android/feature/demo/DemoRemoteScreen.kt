package com.slideremote.android.feature.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slideremote.android.feature.remote.RemoteScreenContent

@Composable
fun DemoRemoteScreen(
    onBack: () -> Unit,
    viewModel: DemoRemoteViewModel = viewModel()
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

