package com.miolib.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- 1. 颜色系统 ---
@Immutable
data class MioColors(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val outline: Color
)

// --- 2. 尺寸系统 ---
@Immutable
data class MioSizes(
    val small: SizeSpec,
    val medium: SizeSpec,
    val large: SizeSpec
)

@Immutable
data class SizeSpec(
    val height: Dp,
    val padding: Dp,
    val fontSize: TextUnit,
    val iconSize: Dp
)

// --- 3. 形状系统 ---
@Immutable
data class MioShapes(
    val cornerSmall: Shape,
    val cornerMedium: Shape,
    val cornerLarge: Shape
)

// --- 4. 新增：排版系统 (Typography) ---
@Immutable
data class MioTypography(
    val display: TextStyle,  // 巨型标题 (如数字展示)
    val titleLarge: TextStyle, // 页面一级标题
    val titleMedium: TextStyle, // 卡片/模块标题
    val body: TextStyle,       // 正文
    val label: TextStyle,      // 按钮/标签文字 (通常加粗)
    val caption: TextStyle     // 辅助说明文字 (小号)
)

// --- 预设值 ---

val LightColors = MioColors(
    primary = Color(0xFF3B82F6),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF3F4F6),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    outline = Color(0xFF9CA3AF)
)

val DarkColors = MioColors(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF9FAFB),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    outline = Color(0xFF475569)
)

val DesktopSizes = MioSizes(
    small = SizeSpec(24.dp, 8.dp, 12.sp, 14.dp),
    medium = SizeSpec(32.dp, 16.dp, 14.sp, 18.dp),
    large = SizeSpec(40.dp, 24.dp, 16.sp, 22.dp)
)

val AndroidSizes = MioSizes(
    small = SizeSpec(32.dp, 12.dp, 12.sp, 16.dp),
    medium = SizeSpec(48.dp, 20.dp, 16.sp, 24.dp),
    large = SizeSpec(56.dp, 28.dp, 18.sp, 28.dp)
)

val RoundedShapes = MioShapes(
    cornerSmall = RoundedCornerShape(4.dp),
    cornerMedium = RoundedCornerShape(8.dp),
    cornerLarge = RoundedCornerShape(16.dp)
)

val SquareShapes = MioShapes(
    cornerSmall = RoundedCornerShape(0.dp),
    cornerMedium = RoundedCornerShape(2.dp),
    cornerLarge = RoundedCornerShape(0.dp)
)

// 定义一套基础的排版规范
val DefaultTypography = MioTypography(
    display = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    body = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    label = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

// --- CompositionLocal 管道 ---
val LocalMioColors = staticCompositionLocalOf { LightColors }
val LocalMioSizes = staticCompositionLocalOf { DesktopSizes }
val LocalMioShapes = staticCompositionLocalOf { RoundedShapes }
val LocalMioTypography = staticCompositionLocalOf { DefaultTypography } // 新增管道

@Composable
fun MioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: MioColors = if (darkTheme) DarkColors else LightColors,
    sizes: MioSizes = MioTheme.sizes,
    shapes: MioShapes = MioTheme.shapes,
    typography: MioTypography = DefaultTypography, // 新增参数
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMioColors provides colors,
        LocalMioSizes provides sizes,
        LocalMioShapes provides shapes,
        LocalMioTypography provides typography // 注入
    ) {
        content()
    }
}

object MioTheme {
    val colors: MioColors @Composable @ReadOnlyComposable get() = LocalMioColors.current
    val sizes: MioSizes @Composable @ReadOnlyComposable get() = LocalMioSizes.current
    val shapes: MioShapes @Composable @ReadOnlyComposable get() = LocalMioShapes.current
    val typography: MioTypography @Composable @ReadOnlyComposable get() = LocalMioTypography.current
}

enum class MioSize { Small, Medium, Large }