package com.miolib.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// 1. 扩展颜色语义：增加文本专用颜色 (onBackground, onSurface)
@Immutable
data class MioColors(
    val primary: Color,       // 主色调 (按钮背景等)
    val onPrimary: Color,     // 主色调上的文本 (按钮文字)
    val background: Color,    // 全局背景色
    val onBackground: Color,  // 全局背景上的文本 (关键！之前缺这个)
    val surface: Color,       // 卡片/容器背景色
    val onSurface: Color,     // 卡片上的文本
    val outline: Color        // 边框颜色 (输入框边框)
)

// 2. 设计新配色：Mio Blue (静谧蓝风格) - 浅色模式
val LightColors = MioColors(
    primary = Color(0xFF3B82F6),      // 鲜亮的现代蓝
    onPrimary = Color(0xFFFFFFFF),    // 蓝底白字
    background = Color(0xFFF3F4F6),   // 极淡的灰蓝色背景，护眼
    onBackground = Color(0xFF111827), // 深灰近黑，高对比度
    surface = Color(0xFFFFFFFF),      // 纯白卡片
    onSurface = Color(0xFF1F2937),    // 卡片文字深灰
    outline = Color(0xFF9CA3AF)       // 浅灰边框
)

// 3. 设计新配色：Deep Night (深空风格) - 深色模式
val DarkColors = MioColors(
    primary = Color(0xFF60A5FA),      // 稍亮的蓝色，在深色背景更清晰
    onPrimary = Color(0xFF0F172A),    // 蓝底深色字
    background = Color(0xFF0F172A),   // 深蓝黑色背景
    onBackground = Color(0xFFF9FAFB), // 浅白文字
    surface = Color(0xFF1E293B),      // 稍微亮一点的深蓝灰卡片
    onSurface = Color(0xFFE2E8F0),    // 卡片文字浅灰
    outline = Color(0xFF475569)       // 深灰边框
)

val LocalMioColors = staticCompositionLocalOf { LightColors }

@Composable
fun MioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: MioColors = if (darkTheme) DarkColors else LightColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMioColors provides colors
    ) {
        content()
    }
}

object MioTheme {
    val colors: MioColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMioColors.current
}