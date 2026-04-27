package com.slideremote.android.core.protocol

data class PairingRequest(
    val deviceName: String,
    val pairingCode: String
)

data class PairingAccepted(
    val sessionId: String,
    val computerName: String
)

data class PairingRejected(
    val reason: String
)

data class PairingQrPayload(
    val app: String,
    val version: Int,
    val mode: String,
    val host: String,
    val port: Int,
    val pairingCode: String
)

data class PairingResponse(
    val accepted: Boolean,
    val sessionId: String? = null,
    val computerName: String? = null,
    val reason: String? = null
)

