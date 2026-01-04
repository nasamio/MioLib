package com.miolib.ui.theme.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class MioColors(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val outline: Color,
    val isLight: Boolean,
)

// --- 新增：21 种主题风格 ---
enum class MioThemeStyle(val label: String, val isDark: Boolean) {
    // === 经典系列 ===
    Light("默认浅", false),
    Dark("默认深", true),

    // === 赛博/暗黑系列 ===
    Cyberpunk("赛博朋克", true), // [New] 强烈的黄黑对比
    Neon("霓虹紫", true),       // 原 Cyber
    Obsidian("黑曜石", true),   // 极致纯黑
    Midnight("午夜蓝", true),
    Forest("暗夜森", true),
    Ruby("红宝石", true),
    Coffee("浓缩咖", true),
    Slate("岩板灰", true),

    // === 清新/明亮系列 ===
    Sakura("樱花粉", false),
    Matcha("抹茶绿", false),
    Ocean("深海蓝", false),     // 虽叫深海，这里做成清爽的深蓝配浅底
    Sky("天空蓝", false),
    Mint("薄荷青", false),
    Lemon("柠檬黄", false),
    Lavender("薰衣草", false),
    Sunset("日落橙", false),
    Grape("葡萄紫", false),     // 浅色底配紫色
    Peach("蜜桃粉", false),
    Gold("黑金奢", true)       // 放在最后压轴
}

// --- 核心：主题颜色工厂 ---
object MioThemeUtils {
    fun getColors(style: MioThemeStyle): MioColors {
        return when (style) {
            // --- 经典 ---
            MioThemeStyle.Light -> lightColors(Color(0xFF3B82F6))
            MioThemeStyle.Dark -> darkColors(Color(0xFF3B82F6))

            // --- 赛博/暗黑 ---
            MioThemeStyle.Cyberpunk -> MioColors(
                primary = Color(0xFFFCEE0A),      // 赛博黄
                onPrimary = Color.Black,          // 黄底黑字
                background = Color(0xFF050505),   // 近乎纯黑
                onBackground = Color(0xFFFCEE0A), // 文字也是黄色(或白)
                surface = Color(0xFF1A1A1A),      // 深灰卡片
                onSurface = Color(0xFFFFFFFF),    // 卡片白字
                outline = Color(0xFFFCEE0A),      // 黄色边框
                isLight = false
            )

            MioThemeStyle.Neon -> MioColors(
                primary = Color(0xFFD946EF),
                onPrimary = Color.White,
                background = Color(0xFF2E1065),
                onBackground = Color(0xFFE9D5FF),
                surface = Color(0xFF4C1D95),
                onSurface = Color(0xFFE9D5FF),
                outline = Color(0xFF7C3AED),
                isLight = false
            )

            MioThemeStyle.Obsidian -> MioColors(
                primary = Color(0xFFFFFFFF),      // 黑白对比
                onPrimary = Color.Black,
                background = Color(0xFF000000),   // 纯黑
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF121212),
                onSurface = Color(0xFFE0E0E0),
                outline = Color(0xFF333333),
                isLight = false
            )

            MioThemeStyle.Midnight -> darkColors(Color(0xFF6366F1), bg = Color(0xFF0F172A), surface = Color(0xFF1E293B))
            MioThemeStyle.Forest -> darkColors(Color(0xFF10B981), bg = Color(0xFF022C22), surface = Color(0xFF064E3B))
            MioThemeStyle.Ruby -> darkColors(Color(0xFFF43F5E), bg = Color(0xFF4C0519), surface = Color(0xFF881337))
            MioThemeStyle.Coffee -> darkColors(Color(0xFFD4A373), bg = Color(0xFF2B2118), surface = Color(0xFF423326))
            MioThemeStyle.Slate -> darkColors(Color(0xFF94A3B8), bg = Color(0xFF0F172A), surface = Color(0xFF334155))
            MioThemeStyle.Gold -> darkColors(Color(0xFFFFD700), bg = Color(0xFF1A1A1A), surface = Color(0xFF2C2C2C))

            // --- 清新 ---
            MioThemeStyle.Sakura -> lightColors(Color(0xFFEC4899), bg = Color(0xFFFDF2F8), surface = Color(0xFFFFF1F2))
            MioThemeStyle.Matcha -> lightColors(Color(0xFF10B981), bg = Color(0xFFF0FDF4), surface = Color(0xFFECFDF5))
            MioThemeStyle.Ocean -> lightColors(Color(0xFF0EA5E9), bg = Color(0xFFF0F9FF), surface = Color(0xFFE0F2FE))
            MioThemeStyle.Sky -> lightColors(Color(0xFF38BDF8), bg = Color(0xFFF0F9FF), surface = Color(0xFFE0F2FE))
            MioThemeStyle.Mint -> lightColors(Color(0xFF2DD4BF), bg = Color(0xFFF0FDFA), surface = Color(0xFFCCFBF1))
            MioThemeStyle.Lemon -> lightColors(Color(0xFFEAB308), bg = Color(0xFFFEFCE8), surface = Color(0xFFFEF9C3))
            MioThemeStyle.Lavender -> lightColors(
                Color(0xFF8B5CF6),
                bg = Color(0xFFF5F3FF),
                surface = Color(0xFFEDE9FE)
            )

            MioThemeStyle.Sunset -> lightColors(Color(0xFFF97316), bg = Color(0xFFFFF7ED), surface = Color(0xFFFFEDD5))
            MioThemeStyle.Grape -> lightColors(Color(0xFF9333EA), bg = Color(0xFFFAF5FF), surface = Color(0xFFF3E8FF))
            MioThemeStyle.Peach -> lightColors(Color(0xFFF87171), bg = Color(0xFFFEF2F2), surface = Color(0xFFFEE2E2))
        }
    }

    // 辅助函数：快速生成浅色主题
    private fun lightColors(
        primary: Color,
        bg: Color = Color(0xFFF3F4F6),
        surface: Color = Color(0xFFFFFFFF),
    ) = MioColors(
        primary = primary,
        onPrimary = Color.White,
        background = bg,
        onBackground = Color(0xFF111827),
        surface = surface,
        onSurface = Color(0xFF1F2937),
        outline = primary.copy(alpha = 0.5f),
        isLight = true
    )

    // 辅助函数：快速生成深色主题
    private fun darkColors(
        primary: Color,
        bg: Color = Color(0xFF0F172A),
        surface: Color = Color(0xFF1E293B),
    ) = MioColors(
        primary = primary,
        onPrimary = Color.Black, // 深色模式下高亮色按钮通常用黑字
        background = bg,
        onBackground = Color(0xFFF1F5F9),
        surface = surface,
        onSurface = Color(0xFFE2E8F0),
        outline = primary.copy(alpha = 0.5f),
        isLight = false
    )
}