package com.example.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AppTypography

private val defaultLightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val defaultDarkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

// Blue Theme
private val lightBlueScheme = lightColorScheme(
    primary = primaryBlueLight,
    onPrimary = onPrimaryBlueLight,
    primaryContainer = primaryContainerBlueLight,
    onPrimaryContainer = onPrimaryContainerBlueLight,
    secondary = secondaryBlueLight,
    onSecondary = onSecondaryBlueLight,
    secondaryContainer = secondaryContainerBlueLight,
    onSecondaryContainer = onSecondaryContainerBlueLight,
    background = backgroundBlueLight,
    onBackground = onBackgroundBlueLight,
    surface = surfaceBlueLight,
    onSurface = onSurfaceBlueLight,
)
private val darkBlueScheme = darkColorScheme(
    primary = primaryBlueDark,
    onPrimary = onPrimaryBlueDark,
    primaryContainer = primaryContainerBlueDark,
    onPrimaryContainer = onPrimaryContainerBlueDark,
    secondary = secondaryBlueDark,
    onSecondary = onSecondaryBlueDark,
    secondaryContainer = secondaryContainerBlueDark,
    onSecondaryContainer = onSecondaryContainerBlueDark,
    background = backgroundBlueDark,
    onBackground = onBackgroundBlueDark,
    surface = surfaceBlueDark,
    onSurface = onSurfaceBlueDark,
)

// Red Theme
private val lightRedScheme = lightColorScheme(
    primary = primaryRedLight,
    onPrimary = onPrimaryRedLight,
    primaryContainer = primaryContainerRedLight,
    onPrimaryContainer = onPrimaryContainerRedLight,
    secondary = secondaryRedLight,
    onSecondary = onSecondaryRedLight,
    secondaryContainer = secondaryContainerRedLight,
    onSecondaryContainer = onSecondaryContainerRedLight,
    background = backgroundRedLight,
    onBackground = onBackgroundRedLight,
    surface = surfaceRedLight,
    onSurface = onSurfaceRedLight,
)
private val darkRedScheme = darkColorScheme(
    primary = primaryRedDark,
    onPrimary = onPrimaryRedDark,
    primaryContainer = primaryContainerRedDark,
    onPrimaryContainer = onPrimaryContainerRedDark,
    secondary = secondaryRedDark,
    onSecondary = onSecondaryRedDark,
    secondaryContainer = secondaryContainerRedDark,
    onSecondaryContainer = onSecondaryContainerRedDark,
    background = backgroundRedDark,
    onBackground = onBackgroundRedDark,
    surface = surfaceRedDark,
    onSurface = onSurfaceRedDark,
)

// Green Theme
private val lightGreenScheme = lightColorScheme(
    primary = primaryGreenLight,
    onPrimary = onPrimaryGreenLight,
    primaryContainer = primaryContainerGreenLight,
    onPrimaryContainer = onPrimaryContainerGreenLight,
    secondary = secondaryGreenLight,
    onSecondary = onSecondaryGreenLight,
    secondaryContainer = secondaryContainerGreenLight,
    onSecondaryContainer = onSecondaryContainerGreenLight,
    background = backgroundGreenLight,
    onBackground = onBackgroundGreenLight,
    surface = surfaceGreenLight,
    onSurface = onSurfaceGreenLight,
)
private val darkGreenScheme = darkColorScheme(
    primary = primaryGreenDark,
    onPrimary = onPrimaryGreenDark,
    primaryContainer = primaryContainerGreenDark,
    onPrimaryContainer = onPrimaryContainerGreenDark,
    secondary = secondaryGreenDark,
    onSecondary = onSecondaryGreenDark,
    secondaryContainer = secondaryContainerGreenDark,
    onSecondaryContainer = onSecondaryContainerGreenDark,
    background = backgroundGreenDark,
    onBackground = onBackgroundGreenDark,
    surface = surfaceGreenDark,
    onSurface = onSurfaceGreenDark,
)

// Purple Theme
private val lightPurpleScheme = lightColorScheme(
    primary = primaryPurpleLight,
    onPrimary = onPrimaryPurpleLight,
    primaryContainer = primaryContainerPurpleLight,
    onPrimaryContainer = onPrimaryContainerPurpleLight,
    secondary = secondaryPurpleLight,
    onSecondary = onSecondaryPurpleLight,
    secondaryContainer = secondaryContainerPurpleLight,
    onSecondaryContainer = onSecondaryContainerPurpleLight,
    background = backgroundPurpleLight,
    onBackground = onBackgroundPurpleLight,
    surface = surfacePurpleLight,
    onSurface = onSurfacePurpleLight,
)
private val darkPurpleScheme = darkColorScheme(
    primary = primaryPurpleDark,
    onPrimary = onPrimaryPurpleDark,
    primaryContainer = primaryContainerPurpleDark,
    onPrimaryContainer = onPrimaryContainerPurpleDark,
    secondary = secondaryPurpleDark,
    onSecondary = onSecondaryPurpleDark,
    secondaryContainer = secondaryContainerPurpleDark,
    onSecondaryContainer = onSecondaryContainerPurpleDark,
    background = backgroundPurpleDark,
    onBackground = onBackgroundPurpleDark,
    surface = surfacePurpleDark,
    onSurface = onSurfacePurpleDark,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

enum class ThemeType {
    Default, Blue, Red, Green, Purple
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    themeType: ThemeType = ThemeType.Default,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.Default -> if (darkTheme) defaultDarkScheme else defaultLightScheme
        ThemeType.Blue -> if (darkTheme) darkBlueScheme else lightBlueScheme
        ThemeType.Red -> if (darkTheme) darkRedScheme else lightRedScheme
        ThemeType.Green -> if (darkTheme) darkGreenScheme else lightGreenScheme
        ThemeType.Purple -> if (darkTheme) darkPurpleScheme else lightPurpleScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

