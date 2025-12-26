package com.miolib.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.miolib.ui.theme.MioTheme

/**
 * MioText: 统一文本组件 (已修复 maxLines 支持)
 */
@Composable
fun MioText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    style: TextStyle = MioTheme.typography.body,
    maxLines: Int = Int.MAX_VALUE, // 新增
    overflow: TextOverflow = TextOverflow.Clip // 新增
) {
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
        fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
        fontWeight = fontWeight ?: style.fontWeight,
        textAlign = textAlign,
        style = style,
        maxLines = maxLines, // 透传
        overflow = overflow  // 透传
    )
}