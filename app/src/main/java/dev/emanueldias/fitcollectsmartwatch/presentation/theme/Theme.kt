package dev.emanueldias.fitcollectsmartwatch.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Typography

private val WearColorScheme: ColorScheme = ColorScheme(
    primary = AndroidGreen,
    onPrimary = AndroidBlack,
    primaryContainer = AndroidGreenDark,
    onPrimaryContainer = AndroidWhite,
    secondary = AndroidGreenLight,
    onSecondary = AndroidBlack,
    secondaryContainer = AndroidGreenDark,
    onSecondaryContainer = AndroidWhite,
    tertiary = AndroidGreenDark,
    onTertiary = AndroidBlack,
    tertiaryContainer = AndroidGreenLight,
    onTertiaryContainer = AndroidBlack,
    background = AndroidBlack,
    onBackground = AndroidWhite,
    surfaceContainer = AndroidSurfaceDark,
    onSurface = AndroidWhite,
    onSurfaceVariant = AndroidLightGray,
    outline = AndroidGreen,
)

@Composable
fun FitCollectSmartwatchTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WearColorScheme,
        typography = Typography(),
        content = content
    )
}
