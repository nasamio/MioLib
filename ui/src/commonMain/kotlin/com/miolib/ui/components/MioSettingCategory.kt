package com.miolib.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

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
 * 可用于纯文本展示，或者作为“按钮”使用（传入 onClick）
 *
 * @param title 标题
 * @param subtitle 副标题（可选）
 * @param icon 左侧图标（可选）
 * @param trailingContent 右侧自定义内容（可选）
 * @param onClick 点击事件，传 null 则不可点击
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
    val finalModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

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
            containerColor = Color.Transparent // 背景透明，便于外部容器（如 Card 或 Column）控制
        ),
        modifier = finalModifier
    )
}

/**
 * MioSettingNavigation: 跳转型设置项
 * 右侧默认带有一个 ">" 箭头，用于进入下一级页面
 *
 * @param info 右侧箭头前的辅助文字（如 WiFi 状态 "已连接"）
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
 * 右侧显示一个 Switch
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
        // 点击整个条目也能切换开关
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
 * 标题和图标在上方，滑块在下方（SupportingContent 位置）
 */
@Composable
fun MioSettingSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    ListItem(
        headlineContent = {
            MioText(
                text = title,
                style = MioTheme.typography.body,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            // 将 Slider 放在副标题位置，实现上下布局
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
        modifier = modifier
    )
}