package com.slideremote.desktop.security

import java.security.SecureRandom
import java.time.Clock

class PairingCodeManager(
    private val clock: Clock = Clock.systemDefaultZone(),
    private val random: SecureRandom = SecureRandom()
) {
    private val lifetimeMillis = 5 * 60 * 1000L

    var currentCode: String = generateCode()
        private set

    private var generatedAtMillis: Long = clock.millis()

    fun generateNewCode(): String {
        currentCode = generateCode()
        generatedAtMillis = clock.millis()
        return currentCode
    }

    fun isValid(code: String): Boolean {
        return code == currentCode && !isExpired()
    }

    fun isExpired(): Boolean {
        return clock.millis() - generatedAtMillis > lifetimeMillis
    }

    fun expiresInSeconds(): Long {
        val remaining = lifetimeMillis - (clock.millis() - generatedAtMillis)
        return (remaining.coerceAtLeast(0L) / 1000L)
    }

    private fun generateCode(): String {
        return random.nextInt(1_000_000).toString().padStart(6, '0')
    }
}

