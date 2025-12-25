package com.miolib.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

@Composable
fun MioTag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MioTheme.colors.primary.copy(alpha = 0.15f),
    textColor: Color = MioTheme.colors.primary
) {
    // 引用 Theme 中的形状 (Tag 通常用 Small 形状)
    val shape = MioTheme.shapes.cornerSmall

    Box(
        modifier = modifier
            .clip(shape) // 动态圆角
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        MioText(
            text = text,
            color = textColor,
            // 修改：使用 Label 样式 (通常是小号加粗)
            style = MioTheme.typography.label
        )
    }
}