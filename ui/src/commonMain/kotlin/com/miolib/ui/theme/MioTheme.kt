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

// --- Light Mode: Mio Blue (回归你喜欢的静谧蓝) ---
val LightColors = MioColors(
    primary = Color(0xFF3B82F6),      // 经典的静谧蓝
    onPrimary = Color(0xFFFFFFFF),    // 蓝底白字
    background = Color(0xFFF3F4F6),   // 护眼的淡灰蓝背景
    onBackground = Color(0xFF111827), // 深灰近黑
    surface = Color(0xFFFFFFFF),      // 纯白卡片
    onSurface = Color(0xFF1F2937),    // 卡片文字深灰
    outline = Color(0xFF9CA3AF)       // 浅灰边框
)

// --- Dark Mode: Deep Cyber (修复对比度) ---
val DarkColors = MioColors(
    primary = Color(0xFF00E5FF),      // 高亮青色
    onPrimary = Color(0xFF000000),    // 青底黑字
    background = Color(0xFF0F172A),   // 午夜深蓝灰
    onBackground = Color(0xFFF1F5F9), // 亮灰白文字
    surface = Color(0xFF1E293B),      // 深蓝灰卡片
    onSurface = Color(0xFF94a3b8),    // 卡片文字灰白
    outline = Color(0xFF94A3B8),      // 浅灰边框
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

// --- 4. 排版系统 ---
@Immutable
data class MioTypography(
    val display: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val body: TextStyle,
    val label: TextStyle,
    val caption: TextStyle
)

// --- 预设值配置 ---

// 桌面端尺寸 (紧凑)
val DesktopSizes = MioSizes(
    small = SizeSpec(28.dp, 10.dp, 12.sp, 14.dp),
    medium = SizeSpec(36.dp, 16.dp, 14.sp, 18.dp),
    large = SizeSpec(44.dp, 24.dp, 16.sp, 22.dp)
)

// 安卓端尺寸 (触摸友好)
val AndroidSizes = MioSizes(
    small = SizeSpec(36.dp, 12.dp, 12.sp, 16.dp),
    medium = SizeSpec(48.dp, 20.dp, 16.sp, 24.dp),
    large = SizeSpec(56.dp, 28.dp, 18.sp, 28.dp)
)

// 形状 (圆润风格)
val RoundedShapes = MioShapes(
    cornerSmall = RoundedCornerShape(6.dp),
    cornerMedium = RoundedCornerShape(12.dp),
    cornerLarge = RoundedCornerShape(20.dp)
)

// 形状 (直角风格)
val SquareShapes = MioShapes(
    cornerSmall = RoundedCornerShape(0.dp),
    cornerMedium = RoundedCornerShape(2.dp),
    cornerLarge = RoundedCornerShape(0.dp)
)

// 排版规范
val DefaultTypography = MioTypography(
    display = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
    body = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    label = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    caption = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp)
)

// --- CompositionLocal 管道 ---
val LocalMioColors = staticCompositionLocalOf { LightColors }
val LocalMioSizes = staticCompositionLocalOf { DesktopSizes }
val LocalMioShapes = staticCompositionLocalOf { RoundedShapes }
val LocalMioTypography = staticCompositionLocalOf { DefaultTypography }

@Composable
fun MioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: MioColors = if (darkTheme) DarkColors else LightColors,
    sizes: MioSizes = MioTheme.sizes,
    shapes: MioShapes = MioTheme.shapes,
    typography: MioTypography = DefaultTypography,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMioColors provides colors,
        LocalMioSizes provides sizes,
        LocalMioShapes provides shapes,
        LocalMioTypography provides typography
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