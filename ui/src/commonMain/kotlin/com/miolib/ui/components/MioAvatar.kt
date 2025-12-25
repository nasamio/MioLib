package com.miolib.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miolib.ui.theme.MioTheme

/**
 * MioAvatar: 圆形头像组件
 * 用于显示用户首字母或图标。
 *
 * @param text 显示的文字 (建议 1-2 个字符，如 "H")
 * @param modifier 修饰符
 * @param size 头像大小，默认 40.dp
 * @param backgroundColor 背景色，默认是 Primary
 * @param textColor 文字颜色，默认是 OnPrimary (白色)
 */
@Composable
fun MioAvatar(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    backgroundColor: Color = MioTheme.colors.primary,
    textColor: Color = MioTheme.colors.onPrimary
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        MioText(
            text = text,
            color = textColor,
            fontSize = (size.value * 0.4).sp, // 字体大小随头像大小自动缩放
            fontWeight = FontWeight.Bold
        )
    }
}