package com.slideremote.android.feature.connection

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.HybridBinarizer
import com.slideremote.android.core.protocol.JsonProtocol
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanScreen(
    onBack: () -> Unit,
    onUseManual: () -> Unit,
    onConnected: () -> Unit,
    viewModel: ConnectionViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var scanned by remember { mutableStateOf(false) }
    var scanError by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR Code") },
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
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (hasPermission) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        CameraQrScanner(
                            enabled = !scanned && !state.connecting,
                            onQrText = { raw ->
                                if (scanned) return@CameraQrScanner
                                scanned = true
                                JsonProtocol.parsePairingQr(raw)
                                    .onSuccess { payload ->
                                        scanError = null
                                        viewModel.connectQr(payload, onConnected)
                                    }
                                    .onFailure {
                                        scanned = false
                                        scanError = "QR Code invalido para o TAPSLIDE."
                                    }
                            }
                        )
                    }
                }
            } else {
                PermissionMessage(
                    onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                )
            }

            Text(
                text = when {
                    state.connecting -> "QR lido. Conectando ao Companion..."
                    state.errorMessage != null -> "Nao foi possivel conectar: ${state.errorMessage}"
                    scanError != null -> scanError.orEmpty()
                    hasPermission -> "Aponte a camera para o QR Code exibido no Companion."
                    else -> "Permita acesso a camera para escanear o QR Code."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.errorMessage != null || scanError != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            OutlinedButton(
                onClick = onUseManual,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Digitar manualmente")
            }
        }
    }
}

@Composable
private fun PermissionMessage(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Permissao de camera",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "O TAPSLIDE usa a camera apenas para ler o QR Code do Companion.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedButton(
            onClick = onRequestPermission,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Permitir camera")
        }
    }
}

@Composable
private fun CameraQrScanner(
    enabled: Boolean,
    onQrText: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { viewContext ->
            PreviewView(viewContext).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(
                                cameraExecutor,
                                QrAnalyzer(
                                    enabled = { enabled },
                                    onQrText = onQrText
                                )
                            )
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                    )
                },
                ContextCompat.getMainExecutor(context)
            )
        }
    )
}

private class QrAnalyzer(
    private val enabled: () -> Boolean,
    private val onQrText: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    override fun analyze(image: ImageProxy) {
        if (!enabled()) {
            image.close()
            return
        }

        val plane = image.planes.firstOrNull()
        if (plane == null) {
            image.close()
            return
        }

        try {
            val data = extractYPlane(plane, image.width, image.height)
            val result = decode(data, image.width, image.height)
                ?: decode(rotateYPlaneClockwise(data, image.width, image.height), image.height, image.width)
            if (result != null) {
                onQrText(result.text)
            }
        } catch (_: NotFoundException) {
            // Frame sem QR Code.
        } catch (_: Exception) {
            // Mantem a camera ativa para a proxima tentativa.
        } finally {
            reader.reset()
            image.close()
        }
    }

    private fun decode(data: ByteArray, width: Int, height: Int): com.google.zxing.Result? {
        return try {
            val source = PlanarYUVLuminanceSource(
                data,
                width,
                height,
                0,
                0,
                width,
                height,
                false
            )
            reader.decodeWithState(BinaryBitmap(HybridBinarizer(source)))
        } catch (_: NotFoundException) {
            null
        }
    }

    private fun extractYPlane(
        plane: ImageProxy.PlaneProxy,
        width: Int,
        height: Int
    ): ByteArray {
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        val output = ByteArray(width * height)
        var outputOffset = 0

        for (row in 0 until height) {
            val rowStart = row * rowStride
            for (col in 0 until width) {
                output[outputOffset++] = buffer.get(rowStart + col * pixelStride)
            }
        }
        return output
    }

    private fun rotateYPlaneClockwise(data: ByteArray, width: Int, height: Int): ByteArray {
        val rotated = ByteArray(data.size)
        var index = 0
        for (x in 0 until width) {
            for (y in height - 1 downTo 0) {
                rotated[index++] = data[y * width + x]
            }
        }
        return rotated
    }
}
