package com.miolib.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// 1. 定义你的颜色系统 (Immutable 提升性能)
@Immutable
data class MioColors(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val surface: Color
)

// 2. 定义默认颜色 (浅色模式)
val LightColors = MioColors(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5)
)

// 3. 定义深色模式颜色
val DarkColors = MioColors(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

// 4. 创建 CompositionLocal (这是“风格一致”的核心魔法)
val LocalMioColors = staticCompositionLocalOf { LightColors }

// 5. 对外暴露的主题入口
@Composable
fun MioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 允许外部传入自定义颜色覆盖默认值，实现“统一修改”
    colors: MioColors = if (darkTheme) DarkColors else LightColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMioColors provides colors
    ) {
        content()
    }
}

// 6. 快捷访问单例 (组件里就用这个)
object MioTheme {
    val colors: MioColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMioColors.current
}