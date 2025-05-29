package com.example.foodly.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color // Required for explicit Color values for error
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = OnBluePrimaryDark,
    primaryContainer = BluePrimaryContainerDark,
    onPrimaryContainer = OnBluePrimaryContainerDark,
    secondary = BlueSecondaryDark,
    onSecondary = OnBlueSecondaryDark,
    secondaryContainer = BlueSecondaryContainerDark,
    onSecondaryContainer = OnBlueSecondaryContainerDark,
    tertiary = AccentDark,
    onTertiary = OnAccentDark,
    tertiaryContainer = AccentContainerDark,
    onTertiaryContainer = OnAccentContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    error = Color(0xFFFFB4AB), // Standard Material error color
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimaryLight,
    onPrimary = OnBluePrimaryLight,
    primaryContainer = BluePrimaryContainerLight,
    onPrimaryContainer = OnBluePrimaryContainerLight,
    secondary = BlueSecondaryLight,
    onSecondary = OnBlueSecondaryLight,
    secondaryContainer = BlueSecondaryContainerLight,
    onSecondaryContainer = OnBlueSecondaryContainerLight,
    tertiary = AccentLight,
    onTertiary = OnAccentLight,
    tertiaryContainer = AccentContainerLight,
    onTertiaryContainer = OnAccentContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    error = Color(0xFFBA1A1A), // Standard Material error color
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)

    /* Other default colors to override if needed
    inversePrimary = ...,
    surfaceTint = ...,
    outlineVariant = ...,
    scrim = ...
    */
)

@Composable
fun FoodlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled for this overhaul to ensure custom theme is applied.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Typography will be updated next
        content = content
        // Consider adding shapes here if you want to customize them globally
        // shapes = Shapes(small = ..., medium = ..., large = ...)
    )
}
