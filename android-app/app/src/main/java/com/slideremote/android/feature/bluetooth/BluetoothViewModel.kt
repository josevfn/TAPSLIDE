package com.slideremote.android.feature.bluetooth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(BluetoothUiState())
    val state: StateFlow<BluetoothUiState> = mutableState
}

data class BluetoothUiState(
    val directModeSupported: Boolean = false
)

