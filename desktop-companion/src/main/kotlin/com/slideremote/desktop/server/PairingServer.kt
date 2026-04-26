package com.slideremote.desktop.server

import com.slideremote.desktop.protocol.PairingAcceptedMessage
import com.slideremote.desktop.protocol.PairingRejectedMessage
import com.slideremote.desktop.security.PairingCodeManager
import com.slideremote.desktop.security.SessionTokenManager
import java.net.InetAddress

class PairingServer(
    private val pairingCodeManager: PairingCodeManager,
    private val sessionTokenManager: SessionTokenManager
) {
    fun pair(deviceName: String?, pairingCode: String?): PairingResult {
        if (deviceName.isNullOrBlank()) {
            return PairingResult.Rejected(PairingRejectedMessage(reason = "MISSING_DEVICE_NAME"))
        }
        if (pairingCode.isNullOrBlank()) {
            return PairingResult.Rejected(PairingRejectedMessage(reason = "MISSING_PAIRING_CODE"))
        }
        if (pairingCodeManager.isExpired()) {
            return PairingResult.Rejected(PairingRejectedMessage(reason = "PAIRING_CODE_EXPIRED"))
        }
        if (!pairingCodeManager.isValid(pairingCode)) {
            return PairingResult.Rejected(PairingRejectedMessage(reason = "INVALID_CODE"))
        }

        val session = sessionTokenManager.createSession(deviceName)
        return PairingResult.Accepted(
            PairingAcceptedMessage(
                sessionId = session.sessionId,
                computerName = computerName()
            )
        )
    }

    private fun computerName(): String {
        return runCatching { InetAddress.getLocalHost().hostName }
            .getOrDefault("Computador")
    }
}

sealed class PairingResult {
    data class Accepted(val message: PairingAcceptedMessage) : PairingResult()
    data class Rejected(val message: PairingRejectedMessage) : PairingResult()
}

