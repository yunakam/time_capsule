package com.example.timecapsule.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6a5f11),
    onPrimary = Color(0xFFffffff),
    primaryContainer = Color(0xFFf4e389),
    onPrimaryContainer = Color(0xFF514700),

    secondary = Color(0xFF655f41),
    onSecondary = Color(0xFFffffff),
    secondaryContainer = Color(0xFFece3bd),
    onSecondaryContainer = Color(0xFF4c472b),

    tertiary = Color(0xFF426650),
    onTertiary = Color(0xFFffffff),
    tertiaryContainer = Color(0xFFc3ecd1),
    onTertiaryContainer = Color(0xFF2a4e3a),

    surface = Color(0xFFfff9eb),
    surfaceDim = Color(0xFFdfdacc),
    surfaceBright = Color(0xFF3c3930),
    onSurface = Color(0xFF1d1c13),
    onSurfaceVariant = Color(0xFFccc6b5),

    outline = Color(0xFF959180),
    outlineVariant = Color(0xFF4a4739),

    error = Color(0xFFba1a1a),
    onError = Color(0xFFffffff),
    errorContainer = Color(0xFFffdad6),
    onErrorContainer = Color(0xFF93000a),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFd7c770),
    onPrimary = Color(0xFF383000),
    primaryContainer = Color(0xFF514700),
    onPrimaryContainer = Color(0xFFf4e389),

    secondary = Color(0xFFcfc7a2),
    onSecondary = Color(0xFF353116),
    secondaryContainer = Color(0xFF4c472b),
    onSecondaryContainer = Color(0xFFece3bd),

    tertiary = Color(0xFFa8d0b5),
    onTertiary = Color(0xFF123724),
    tertiaryContainer = Color(0xFF2a4e3a),
    onTertiaryContainer = Color(0xFFc3ecd1),

    surface = Color(0xFF15130c),
    surfaceDim = Color(0xFF15130c),
    surfaceBright = Color(0xFF3c3930),
    onSurface = Color(0xFFe8e2d4),
    onSurfaceVariant = Color(0xFFccc6b5),

    outline = Color(0xFF959180),
    outlineVariant = Color(0xFF4a4739),

    error = Color(0xFFffb4ab),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000a),
    onErrorContainer = Color(0xFFffdad6a),
)

@Composable
fun TimeCapsuleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    println("TimeCapsuleTheme is being used!")

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
        typography = Typography,
        content = content
    )
}