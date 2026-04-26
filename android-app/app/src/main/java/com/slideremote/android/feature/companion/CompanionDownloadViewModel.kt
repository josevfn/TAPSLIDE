package com.slideremote.android.feature.companion

import androidx.lifecycle.ViewModel
import com.slideremote.android.core.links.AppLinks

class CompanionDownloadViewModel : ViewModel() {
    val downloadUrl: String = AppLinks.COMPANION_DOWNLOAD_URL
    val displayUrl: String = downloadUrl.removePrefix("https://")
    val shareText: String = "Baixe o Slide Remote Companion para computador:\n$downloadUrl"
}

