package com.slideremote.desktop.security

import java.util.UUID

class SessionTokenManager {
    private var activeSession: ActiveSession? = null

    fun createSession(deviceName: String): ActiveSession {
        val session = ActiveSession(
            sessionId = UUID.randomUUID().toString(),
            deviceName = deviceName
        )
        activeSession = session
        return session
    }

    fun validate(sessionId: String?): Boolean {
        return sessionId != null && activeSession?.sessionId == sessionId
    }

    fun current(): ActiveSession? = activeSession

    fun disconnect() {
        activeSession = null
    }
}

data class ActiveSession(
    val sessionId: String,
    val deviceName: String
)

