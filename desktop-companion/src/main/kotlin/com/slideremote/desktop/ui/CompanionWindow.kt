package com.slideremote.desktop.ui

import com.slideremote.desktop.input.KeyboardController
import com.slideremote.desktop.protocol.PairingQrPayload
import com.slideremote.desktop.protocol.Protocol
import com.slideremote.desktop.qr.QrCodeGenerator
import com.slideremote.desktop.security.PairingCodeManager
import com.slideremote.desktop.security.SessionTokenManager
import com.slideremote.desktop.server.PairingServer
import com.slideremote.desktop.server.WifiWebSocketServer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.WindowConstants

class CompanionWindow : WifiWebSocketServer.Listener {
    private val json = Json { encodeDefaults = true }
    private val pairingCodeManager = PairingCodeManager()
    private val sessionTokenManager = SessionTokenManager()
    private val pairingServer = PairingServer(pairingCodeManager, sessionTokenManager)
    private val keyboardController = KeyboardController()
    private val qrCodeGenerator = QrCodeGenerator()

    private val port = Protocol.DEFAULT_PORT
    private var localIp = findLocalIpAddress()
    private var server = createServer()

    private val frame = JFrame("Slide Remote Companion")
    private val statusLabel = JLabel("Status: parado")
    private val ipLabel = JLabel("IP local: $localIp")
    private val portLabel = JLabel("Porta: $port")
    private val codeLabel = JLabel("Codigo: ${pairingCodeManager.currentCode}")
    private val expiresLabel = JLabel("Expira em: ${pairingCodeManager.expiresInSeconds()}s")
    private val deviceLabel = JLabel("Dispositivo conectado: nenhum")
    private val lastCommandLabel = JLabel("Ultimo comando: -")
    private val qrLabel = JLabel()
    private val logArea = JTextArea(8, 40)

    private val startButton = JButton("Iniciar servidor")
    private val stopButton = JButton("Parar servidor")
    private val newCodeButton = JButton("Gerar novo codigo")
    private val disconnectButton = JButton("Desconectar celular")

    private val expirationTimer = Timer(1_000) {
        expiresLabel.text = "Expira em: ${pairingCodeManager.expiresInSeconds()}s"
    }

    fun show() {
        configureFrame()
        updateQrCode()
        expirationTimer.start()
        frame.isVisible = true
    }

    private fun configureFrame() {
        frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        frame.minimumSize = Dimension(560, 680)
        frame.contentPane = buildContent()
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                expirationTimer.stop()
                if (server.isRunning()) {
                    server.stop()
                }
            }
        })

        startButton.addActionListener {
            localIp = findLocalIpAddress()
            ipLabel.text = "IP local: $localIp"
            if (!server.isRunning()) {
                server.start()
            }
            updateButtons()
            updateQrCode()
        }
        stopButton.addActionListener {
            server.stop()
            updateButtons()
        }
        newCodeButton.addActionListener {
            pairingCodeManager.generateNewCode()
            sessionTokenManager.disconnect()
            codeLabel.text = "Codigo: ${pairingCodeManager.currentCode}"
            deviceLabel.text = "Dispositivo conectado: nenhum"
            appendLog("Novo codigo de pareamento gerado.")
            updateQrCode()
        }
        disconnectButton.addActionListener {
            sessionTokenManager.disconnect()
            deviceLabel.text = "Dispositivo conectado: nenhum"
            statusLabel.text = if (server.isRunning()) "Status: aguardando conexao" else "Status: parado"
            appendLog("Celular desconectado.")
        }
        updateButtons()
    }

    private fun buildContent(): JPanel {
        val root = JPanel(BorderLayout(16, 16)).apply {
            border = BorderFactory.createEmptyBorder(18, 18, 18, 18)
            background = Color(0xF8FAF8)
        }

        val title = JLabel("Slide Remote Companion").apply {
            font = font.deriveFont(Font.BOLD, 24f)
        }

        val infoPanel = JPanel(GridBagLayout()).apply {
            background = root.background
        }
        val constraints = GridBagConstraints().apply {
            gridx = 0
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            insets = Insets(3, 0, 3, 0)
        }
        listOf(statusLabel, ipLabel, portLabel, codeLabel, expiresLabel, deviceLabel, lastCommandLabel).forEachIndexed { index, label ->
            label.font = label.font.deriveFont(15f)
            constraints.gridy = index
            infoPanel.add(label, constraints)
        }

        val topPanel = JPanel(BorderLayout(0, 12)).apply {
            background = root.background
            add(title, BorderLayout.NORTH)
            add(infoPanel, BorderLayout.CENTER)
        }

        val centerPanel = JPanel(BorderLayout()).apply {
            background = root.background
            border = BorderFactory.createEmptyBorder(8, 0, 8, 0)
            qrLabel.horizontalAlignment = JLabel.CENTER
            add(qrLabel, BorderLayout.CENTER)
        }

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = root.background
            listOf(startButton, stopButton, newCodeButton, disconnectButton).forEach {
                it.maximumSize = Dimension(Int.MAX_VALUE, 38)
                it.alignmentX = JButton.CENTER_ALIGNMENT
                add(it)
            }
        }

        logArea.isEditable = false
        logArea.lineWrap = true
        logArea.wrapStyleWord = true
        val logScroll = JScrollPane(logArea).apply {
            border = BorderFactory.createTitledBorder("Logs")
        }

        val bottomPanel = JPanel(BorderLayout(0, 12)).apply {
            background = root.background
            add(buttonPanel, BorderLayout.NORTH)
            add(logScroll, BorderLayout.CENTER)
        }

        root.add(topPanel, BorderLayout.NORTH)
        root.add(centerPanel, BorderLayout.CENTER)
        root.add(bottomPanel, BorderLayout.SOUTH)
        return root
    }

    private fun createServer(): WifiWebSocketServer {
        return WifiWebSocketServer(
            port = port,
            pairingServer = pairingServer,
            sessionTokenManager = sessionTokenManager,
            keyboardController = keyboardController,
            listener = this
        )
    }

    private fun updateButtons() {
        val running = server.isRunning()
        startButton.isEnabled = !running
        stopButton.isEnabled = running
    }

    private fun updateQrCode() {
        val payload = PairingQrPayload(
            host = localIp,
            port = port,
            pairingCode = pairingCodeManager.currentCode
        )
        val image = qrCodeGenerator.generate(json.encodeToString(payload))
        qrLabel.icon = ImageIcon(image)
    }

    override fun onStatusChanged(status: String) = onUi {
        statusLabel.text = "Status: $status"
    }

    override fun onConnected(deviceName: String) = onUi {
        deviceLabel.text = "Dispositivo conectado: $deviceName"
    }

    override fun onDisconnected() = onUi {
        deviceLabel.text = "Dispositivo conectado: nenhum"
    }

    override fun onCommand(command: String, timestamp: Long) = onUi {
        val time = SimpleDateFormat("HH:mm:ss").format(Date(timestamp))
        lastCommandLabel.text = "Ultimo comando: $command as $time"
        appendLog("Comando recebido: $command as $time.")
    }

    override fun onLog(message: String) = onUi {
        appendLog(message)
    }

    private fun appendLog(message: String) {
        val time = SimpleDateFormat("HH:mm:ss").format(Date())
        logArea.append("[$time] $message\n")
        logArea.caretPosition = logArea.document.length
    }

    private fun onUi(block: () -> Unit) {
        if (SwingUtilities.isEventDispatchThread()) {
            block()
        } else {
            SwingUtilities.invokeLater(block)
        }
    }

    private fun findLocalIpAddress(): String {
        return runCatching {
            NetworkInterface.getNetworkInterfaces()
                .asSequence()
                .filter { it.isUp && !it.isLoopback && !it.isVirtual }
                .flatMap { it.inetAddresses.asSequence() }
                .filterIsInstance<Inet4Address>()
                .firstOrNull { !it.isLoopbackAddress }
                ?.hostAddress
        }.getOrNull() ?: "127.0.0.1"
    }
}

