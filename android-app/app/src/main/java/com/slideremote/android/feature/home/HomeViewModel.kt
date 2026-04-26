package com.slideremote.android.feature.home

import androidx.lifecycle.ViewModel
import com.slideremote.android.core.model.ConnectionMode

class HomeViewModel : ViewModel() {
    val modes: List<ConnectionMode> = listOf(
        ConnectionMode.BluetoothDirect,
        ConnectionMode.WifiHotspot,
        ConnectionMode.Demo
    )
}

