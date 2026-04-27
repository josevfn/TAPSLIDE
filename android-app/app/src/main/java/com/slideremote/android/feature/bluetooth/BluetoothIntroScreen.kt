package com.slideremote.android.feature.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slideremote.android.ui.components.PrimaryActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothIntroScreen(
    onBack: () -> Unit,
    onUseWifi: () -> Unit,
    onUnsupported: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bluetooth direto") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Bluetooth direto e experimental",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Seu celular pode ter Bluetooth e ainda assim nao permitir que um app funcione como teclado ou apresentador Bluetooth. No Android, esse modo depende de suporte HID liberado pelo sistema/fabricante.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            PrimaryActionButton(
                text = "Entendi",
                onClick = onUnsupported,
                icon = Icons.Default.Bluetooth,
                modifier = Modifier.padding(top = 28.dp)
            )
            PrimaryActionButton(
                text = "Usar Wi-Fi / Hotspot",
                onClick = onUseWifi,
                icon = Icons.Default.Wifi,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
