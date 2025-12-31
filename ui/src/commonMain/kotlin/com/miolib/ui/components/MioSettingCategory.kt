package com.miolib.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

/**
 * 内部扩展：统一的设置项交互样式
 * 处理了 Hover 背景动画、点击波纹绑定
 */
@Composable
private fun Modifier.applySettingItemStyle(
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }

    // 1. 获取悬停状态
    val isHovered by interactionSource.collectIsHoveredAsState()

    // 2. 背景色动画
    val targetColor = if (isHovered && enabled) {
        MioTheme.colors.onSurface.copy(alpha = 0.05f)
    } else {
        Color.Transparent
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(150)
    )

    // 3. 组合 Modifier
    return this
        .fillMaxWidth()
        .background(animatedColor)
        .let { modifier ->
            if (onClick != null) {
                modifier.clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(),
                    enabled = enabled,
                    onClick = onClick
                )
            } else {
                modifier.hoverable(
                    interactionSource = interactionSource,
                    enabled = enabled
                )
            }
        }
        .padding(horizontal = 16.dp, vertical = 4.dp)
}

/**
 * MioSettingCategory: 设置页的分组标题
 */
@Composable
fun MioSettingCategory(
    text: String,
    modifier: Modifier = Modifier
) {
    MioText(
        text = text,
        style = MioTheme.typography.label,
        color = MioTheme.colors.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 24.dp, bottom = 8.dp, end = 16.dp)
    )
}

/**
 * MioSettingItem: 基础通用设置项
 */
@Composable
fun MioSettingItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    titleColor: Color = MioTheme.colors.onSurface
) {
    ListItem(
        headlineContent = {
            MioText(
                text = title,
                style = MioTheme.typography.body,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
        },
        supportingContent = if (subtitle != null) {
            {
                MioText(
                    text = subtitle,
                    style = MioTheme.typography.caption,
                    color = MioTheme.colors.outline
                )
            }
        } else null,
        leadingContent = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MioTheme.colors.onSurface
                )
            }
        } else null,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = modifier.applySettingItemStyle(onClick = onClick)
    )
}

/**
 * MioSettingNavigation: 跳转型设置项
 */
@Composable
fun MioSettingNavigation(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    info: String? = null
) {
    MioSettingItem(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        onClick = onClick,
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (info != null) {
                    MioText(
                        text = info,
                        style = MioTheme.typography.caption,
                        color = MioTheme.colors.outline,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Enter",
                    tint = MioTheme.colors.outline
                )
            }
        }
    )
}

/**
 * MioSettingSwitch: 开关型设置项
 */
@Composable
fun MioSettingSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    MioSettingItem(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        onClick = if (enabled) { { onCheckedChange(!checked) } } else null,
        trailingContent = {
            MioSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    )
}

/**
 * MioSettingSlider: 拖动调节型设置项
 * [优化]
 * 1. 增加了当前数值显示 (百分比)
 * 2. 优化了布局，将标题和数值放在同一行，视觉更平衡
 */
@Composable
fun MioSettingSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    // [新增] 可选：自定义数值格式化逻辑
    valueFormatter: ((Float) -> String)? = null
) {
    // 计算显示的数值字符串 (默认显示百分比)
    val displayValue = valueFormatter?.invoke(value) ?: run {
        val percent = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start) * 100).toInt()
        "$percent%"
    }

    ListItem(
        headlineContent = {
            // [修改] 使用 Row 将标题和数值分开排列，两端对齐
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MioText(
                    text = title,
                    style = MioTheme.typography.body,
                    fontWeight = FontWeight.Medium
                )
                MioText(
                    text = displayValue,
                    style = MioTheme.typography.body,
                    fontWeight = FontWeight.Medium,
                    color = MioTheme.colors.primary // 高亮数值
                )
            }
        },
        supportingContent = {
            MioSlider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingContent = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MioTheme.colors.onSurface
                )
            }
        } else null,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        // 这里 onClick 传 null，但 modifier 依然会处理 Hover 效果
        modifier = modifier.applySettingItemStyle(onClick = null)
    )
}