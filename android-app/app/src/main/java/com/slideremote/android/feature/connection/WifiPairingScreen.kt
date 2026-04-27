package com.slideremote.android.feature.connection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slideremote.android.ui.components.PrimaryActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiPairingScreen(
    onBack: () -> Unit,
    onScanQr: () -> Unit,
    onConnected: () -> Unit,
    viewModel: ConnectionViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conectar ao Companion") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            PrimaryActionButton(
                text = "Escanear QR Code",
                onClick = onScanQr,
                icon = Icons.Default.QrCodeScanner
            )

            Text(
                text = "ou",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            ManualPairingFields(
                state = state,
                onHostChange = viewModel::updateHost,
                onPortChange = viewModel::updatePort,
                onPairingCodeChange = viewModel::updatePairingCode
            )

            PrimaryActionButton(
                text = if (state.connecting) "Conectando..." else "Conectar manualmente",
                onClick = { viewModel.connectManual(onConnected) },
                enabled = state.canConnect && !state.connecting
            )

            state.errorMessage?.let { error ->
                Text(
                    text = "Nao foi possivel conectar: $error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedButton(
                onClick = viewModel::toggleHotspotInstructions,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.WifiTethering, contentDescription = null)
                Text("Usar hotspot do celular", modifier = Modifier.padding(start = 8.dp))
            }

            AnimatedVisibility(visible = state.showHotspotInstructions) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Ative o roteador/hotspot do celular.", fontWeight = FontWeight.SemiBold)
                    Text("2. Conecte o notebook a essa rede.")
                    Text("3. Abra o Companion no notebook.")
                    Text("4. Escaneie o QR Code exibido.")
                }
            }
        }
    }
}
