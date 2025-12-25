package com.miolib.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.miolib.ui.theme.MioTheme

/**
 * MioSwitch: 风格统一的开关组件
 * 去除了点击时的光圈涟漪，视觉更简洁。
 *
 * @param checked 开关是否开启
 * @param onCheckedChange 点击回调，返回新的布尔值
 * @param modifier 外部修饰符
 * @param enabled 是否可用
 */
@Composable
fun MioSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // 创建一个无状态的交互源，用于消除点击时的灰色圆圈(Ripple)
    val interactionSource = remember { MutableInteractionSource() }

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        // 关键点：thumbContent 传 null 使用默认圆点
        thumbContent = null,
        colors = SwitchDefaults.colors(
            // --- 选中状态 ---
            checkedThumbColor = Color.White, // 小圆球颜色
            checkedTrackColor = MioTheme.colors.primary, // 轨道变蓝
            checkedBorderColor = Color.Transparent, // 去掉选中时的边框

            // --- 未选中状态 ---
            uncheckedThumbColor = MioTheme.colors.outline, // 小圆球灰色
            uncheckedTrackColor = MioTheme.colors.surface, // 轨道是大背景色
            uncheckedBorderColor = MioTheme.colors.outline.copy(alpha = 0.5f), // 边框淡一点

            // --- 消除原本的大阴影(Overlay) ---
            // 虽然 interactionSource 已经处理了大部分，但这里为了保险把 Overlay 设为透明
            uncheckedIconColor = Color.Transparent
        )
    )
}

/**
 * MioCheckBox: 风格统一的复选框
 *
 * @param checked 是否选中
 * @param onCheckedChange 点击回调
 * @param modifier 修饰符
 * @param enabled 是否可用
 */
@Composable
fun MioCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = MioTheme.colors.primary,
            uncheckedColor = MioTheme.colors.outline,
            checkmarkColor = Color.White
        )
    )
}