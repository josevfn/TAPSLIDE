package com.slideremote.android.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slideremote.android.core.model.ConnectionMode
import com.slideremote.android.ui.components.ConnectionModeCard

@Composable
fun HomeScreen(
    onBluetoothDirect: () -> Unit,
    onWifiHotspot: () -> Unit,
    onDemo: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Slide Remote",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Controle seus slides pelo celular.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(34.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            viewModel.modes.forEach { mode ->
                val icon = when (mode) {
                    ConnectionMode.BluetoothDirect -> Icons.Default.Bluetooth
                    ConnectionMode.WifiHotspot -> Icons.Default.Wifi
                    ConnectionMode.Demo -> Icons.Default.PlayArrow
                }
                val action = when (mode) {
                    ConnectionMode.BluetoothDirect -> onBluetoothDirect
                    ConnectionMode.WifiHotspot -> onWifiHotspot
                    ConnectionMode.Demo -> onDemo
                }
                ConnectionModeCard(
                    title = mode.title,
                    subtitle = mode.subtitle,
                    icon = icon,
                    onClick = action
                )
            }
        }
    }
}

