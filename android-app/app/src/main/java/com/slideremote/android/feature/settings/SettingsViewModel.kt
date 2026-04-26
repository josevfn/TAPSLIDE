package com.slideremote.android.feature.settings

import androidx.lifecycle.ViewModel
import com.slideremote.android.core.links.AppLinks
import com.slideremote.android.core.model.AppSettings
import com.slideremote.android.core.storage.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val repository: SettingsRepository = SettingsRepository()
) : ViewModel() {
    val settings: StateFlow<AppSettings> = repository.settings
    val companionLink: String = AppLinks.COMPANION_DOWNLOAD_URL

    fun setKeepScreenOn(enabled: Boolean) = repository.setKeepScreenOn(enabled)
    fun setVibrateOnTap(enabled: Boolean) = repository.setVibrateOnTap(enabled)
    fun setGesturesEnabled(enabled: Boolean) = repository.setGesturesEnabled(enabled)
    fun clearLastDevice() = repository.clearLastDevice()
}

