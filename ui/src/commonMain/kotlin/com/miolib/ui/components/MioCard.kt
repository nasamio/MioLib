package com.miolib.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MioCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MioTheme.colors.surface,
    contentColor: Color = MioTheme.colors.onSurface,
    elevation: Dp = 4.dp,
    hasBorder: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderStroke = if (hasBorder) {
        BorderStroke(1.dp, MioTheme.colors.outline.copy(alpha = 0.2f))
    } else null

    // 关键：引用 Theme 中的 Large 形状 (根据配置可能是 16.dp 也可能是 0.dp)
    val shape = MioTheme.shapes.cornerLarge

    val colors = CardDefaults.cardColors(
        containerColor = backgroundColor,
        contentColor = contentColor
    )
    val elevationObj = CardDefaults.cardElevation(defaultElevation = elevation)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            border = borderStroke,
            colors = colors,
            elevation = elevationObj
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            border = borderStroke,
            colors = colors,
            elevation = elevationObj
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}