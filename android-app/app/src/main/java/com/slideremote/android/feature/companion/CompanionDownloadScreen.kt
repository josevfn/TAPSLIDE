package com.slideremote.android.feature.companion

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slideremote.android.ui.components.HelpCard
import com.slideremote.android.ui.components.PrimaryActionButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionDownloadScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: CompanionDownloadViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Usar Wi-Fi / Hotspot") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Abra o Companion no computador para conectar pelo QR Code.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CompanionInstructionsCard(displayUrl = viewModel.displayUrl)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(viewModel.downloadUrl))
                        scope.launch { snackbarHostState.showSnackbar("Link copiado.") }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Text("Copiar", modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, viewModel.shareText)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Compartilhar link"))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Text("Compartilhar", modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedButton(
                onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.downloadUrl)))
                    } catch (_: ActivityNotFoundException) {
                        scope.launch { snackbarHostState.showSnackbar("Nao foi possivel abrir o navegador.") }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                Text("Abrir link", modifier = Modifier.padding(start = 8.dp))
            }

            PrimaryActionButton(
                text = "Ja abri o Companion",
                onClick = onContinue,
                icon = Icons.Default.Check
            )

            HelpCard(
                title = "O que e o Companion?",
                body = "O Companion e um pequeno programa oficial para Windows. Ele recebe os comandos do celular pela rede local ou hotspot e envia as teclas para o PowerPoint, Google Slides ou LibreOffice. Ele nao exige conta e funciona apenas durante a apresentacao."
            )
            HelpCard(
                title = "Quando preciso usar isso?",
                body = "Use este modo quando quiser uma conexao mais estavel, quando o Bluetooth direto nao funcionar no seu aparelho ou quando preferir conectar pelo QR Code."
            )
        }
    }
}
