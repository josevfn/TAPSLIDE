package com.slideremote.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    secondary = BlueSecondary,
    tertiary = GreenTertiary,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    surfaceVariant = SurfaceSoft,
    onSurface = Ink,
    onSurfaceVariant = MutedInk,
    error = Warning
)

private val DarkColors = darkColorScheme(
    primary = ColorTokens.DarkPrimary,
    onPrimary = Ink,
    secondary = ColorTokens.DarkSecondary,
    tertiary = ColorTokens.DarkTertiary,
    background = DarkSurface,
    onBackground = Paper,
    surface = DarkSurface,
    surfaceVariant = ColorTokens.DarkSurfaceVariant,
    onSurface = Paper,
    onSurfaceVariant = ColorTokens.DarkMutedInk,
    error = ColorTokens.DarkWarning
)

@Composable
fun SlideRemoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}

private object ColorTokens {
    val DarkPrimary = androidx.compose.ui.graphics.Color(0xFF7CCDC2)
    val DarkSecondary = androidx.compose.ui.graphics.Color(0xFFA7C8EF)
    val DarkTertiary = androidx.compose.ui.graphics.Color(0xFFB9D6A7)
    val DarkSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF243030)
    val DarkMutedInk = androidx.compose.ui.graphics.Color(0xFFC0CACA)
    val DarkWarning = androidx.compose.ui.graphics.Color(0xFFE9C16F)
}

