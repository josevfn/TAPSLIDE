package com.slideremote.android.core.model

data class AppSettings(
    val keepScreenOn: Boolean = true,
    val vibrateOnTap: Boolean = true,
    val clickSound: Boolean = false,
    val gesturesEnabled: Boolean = true,
    val leftHandedMode: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System,
    val lastDeviceName: String? = null
)

enum class ThemeMode {
    System,
    Light,
    Dark
}

