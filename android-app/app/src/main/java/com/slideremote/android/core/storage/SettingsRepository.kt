package com.slideremote.android.core.storage

import com.slideremote.android.core.model.AppSettings
import com.slideremote.android.core.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SettingsRepository {
    private val mutableSettings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = mutableSettings

    fun setKeepScreenOn(enabled: Boolean) {
        mutableSettings.update { it.copy(keepScreenOn = enabled) }
    }

    fun setVibrateOnTap(enabled: Boolean) {
        mutableSettings.update { it.copy(vibrateOnTap = enabled) }
    }

    fun setGesturesEnabled(enabled: Boolean) {
        mutableSettings.update { it.copy(gesturesEnabled = enabled) }
    }

    fun setThemeMode(mode: ThemeMode) {
        mutableSettings.update { it.copy(themeMode = mode) }
    }

    fun clearLastDevice() {
        mutableSettings.update { it.copy(lastDeviceName = null) }
    }
}

