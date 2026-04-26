package com.slideremote.android.core.model

data class PairedDevice(
    val id: String,
    val name: String,
    val host: String? = null,
    val port: Int? = null,
    val lastConnectedAtMillis: Long? = null
)

