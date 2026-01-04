package com.miolib.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.miolib.ui.theme.MioTheme
import com.miolib.ui.theme.state.MioSize

/**
 * MioButton: 支持多尺寸和自定义形状的按钮
 *
 * @param size 尺寸枚举 (Small, Medium, Large)
 */
@Composable
fun MioButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: MioSize = MioSize.Medium, // 默认为中等
    enabled: Boolean = true,
    backgroundColor: Color = MioTheme.colors.primary,
    contentColor: Color = MioTheme.colors.onPrimary,
) {
    // 1. 从 Theme 获取当前的尺寸配置规范
    val sizeSpec = when (size) {
        MioSize.Small -> MioTheme.sizes.small
        MioSize.Medium -> MioTheme.sizes.medium
        MioSize.Large -> MioTheme.sizes.large
    }

    // 2. 从 Theme 获取当前的形状规范 (这里用 Medium 形状)
    val shape = MioTheme.shapes.cornerMedium

    Row(
        modifier = modifier
            .height(sizeSpec.height) // 动态高度
            .clip(shape) // 动态圆角
            .background(if (enabled) backgroundColor else MioTheme.colors.outline)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White)
            )
            .padding(horizontal = sizeSpec.padding), // 动态内边距
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = sizeSpec.fontSize, // 动态字体大小
            fontWeight = FontWeight.Bold
        )
    }
}