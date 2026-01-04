package com.miolib.ui.theme

import androidx.compose.runtime.*
import com.miolib.ui.theme.state.*

// --- CompositionLocal 管道 ---
val LocalMioColors = staticCompositionLocalOf { MioThemeUtils.getColors(MioThemeStyle.Light) }
val LocalMioSizes = staticCompositionLocalOf { DesktopSizes }
val LocalMioShapes = staticCompositionLocalOf { RoundedShapes }
val LocalMioTypography = staticCompositionLocalOf { DefaultTypography }

object MioTheme {
    val colors: MioColors @Composable @ReadOnlyComposable get() = LocalMioColors.current
    val sizes: MioSizes @Composable @ReadOnlyComposable get() = LocalMioSizes.current
    val shapes: MioShapes @Composable @ReadOnlyComposable get() = LocalMioShapes.current
    val typography: MioTypography @Composable @ReadOnlyComposable get() = LocalMioTypography.current
}

@Composable
fun MioTheme(
    style: MioThemeStyle = MioThemeStyle.Light,
    sizes: MioSizes = MioTheme.sizes,
    shapes: MioShapes = MioTheme.shapes,
    typography: MioTypography = DefaultTypography,
    content: @Composable () -> Unit,
) {
    val colors = remember(style) { MioThemeUtils.getColors(style) }
    CompositionLocalProvider(
        LocalMioColors provides colors,
        LocalMioSizes provides sizes,
        LocalMioShapes provides shapes,
        LocalMioTypography provides typography
    ) {
        content()
    }
}

