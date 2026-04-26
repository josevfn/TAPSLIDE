package com.slideremote.android.core.protocol

object ProtocolConstants {
    const val APP_ID = "slide-remote"
    const val VERSION = 1
    const val DEFAULT_PORT = 8765

    const val TYPE_COMMAND = "command"
    const val TYPE_PAIRING_REQUEST = "pairing_request"
    const val TYPE_PAIRING_ACCEPTED = "pairing_accepted"
    const val TYPE_PAIRING_REJECTED = "pairing_rejected"
    const val TYPE_HEARTBEAT = "heartbeat"
}

