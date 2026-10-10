package com.asar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AsarColorScheme = lightColorScheme(
    primary = AsarPrimary,
    onPrimary = AsarSurface,
    primaryContainer = AsarPrimarySoft,
    onPrimaryContainer = AsarPrimaryDark,
    secondary = AsarInk,
    onSecondary = AsarSurface,
    secondaryContainer = AsarSurfaceSoft,
    onSecondaryContainer = AsarPrimaryDark,
    tertiary = AsarAmber,
    onTertiary = AsarSurface,
    tertiaryContainer = AsarSandSoft,
    onTertiaryContainer = AsarAmber,
    background = AsarBackground,
    onBackground = AsarInk,
    surface = AsarSurface,
    onSurface = AsarInk,
    surfaceVariant = AsarSurfaceSoft,
    onSurfaceVariant = AsarInkMuted,
    surfaceContainerLowest = AsarSurface,
    surfaceContainerLow = AsarBackground,
    surfaceContainer = AsarSurface,
    surfaceContainerHigh = AsarSurface,
    surfaceContainerHighest = AsarSurfaceSoft,
    surfaceTint = AsarSurface,
    outline = AsarOutlineStrong,
    outlineVariant = AsarOutline,
    error = AsarDanger,
    onError = AsarSurface,
    errorContainer = AsarDangerSoft,
    onErrorContainer = AsarDanger,
)

/**
 * ASAR uses a single, carefully tuned light palette. Dynamic color is deliberately
 * disabled so the brand looks identical on every device.
 */
@Composable
fun AsarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AsarColorScheme,
        typography = AsarTypography,
        shapes = AsarShapes,
        content = content
    )
}
