package com.miolib.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.miolib.ui.theme.MioTheme

/**
 * MioText: 统一文本组件
 *
 * @param style 文字样式 (语义化)，默认为 Body。建议优先使用 MioTheme.typography.xxx
 * @param color 文字颜色，默认为 Unspecified (将自动根据背景色计算或继承)
 */
@Composable
fun MioText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    // 关键改动：默认样式引用 Theme 中的 Body
    style: TextStyle = MioTheme.typography.body
) {
    // 颜色处理逻辑：
    // 1. 如果传了 color，用 color
    // 2. 如果 style 里自带 color (且不是 Unspecified)，用 style 的
    // 3. 否则兜底使用 onSurface (深色文字)
    val finalColor = if (color != Color.Unspecified) {
        color
    } else if (style.color != Color.Unspecified) {
        style.color
    } else {
        MioTheme.colors.onSurface
    }

    Text(
        text = text,
        modifier = modifier,
        color = finalColor,
        // 允许外部覆盖 style 中的 fontSize
        fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
        // 允许外部覆盖 style 中的 fontWeight
        fontWeight = fontWeight ?: style.fontWeight,
        textAlign = textAlign,
        style = style
    )
}