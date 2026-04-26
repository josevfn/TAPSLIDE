package com.slideremote.android.core.protocol

import com.slideremote.android.core.model.RemoteCommand

data class CommandMessage(
    val sessionId: String,
    val command: RemoteCommand,
    val timestamp: Long = System.currentTimeMillis()
)

