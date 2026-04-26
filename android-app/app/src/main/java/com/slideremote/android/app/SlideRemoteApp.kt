package com.slideremote.android.app

import androidx.compose.runtime.Composable
import com.slideremote.android.ui.theme.SlideRemoteTheme

@Composable
fun SlideRemoteApp() {
    SlideRemoteTheme {
        AppNavHost()
    }
}

