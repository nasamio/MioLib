package com.miolib.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

@Composable
fun MioCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MioTheme.colors.surface,
    contentColor: Color = MioTheme.colors.onPrimary,
    elevation: Dp = 4.dp,
    hasBorder: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderStroke = if (hasBorder) {
        BorderStroke(1.dp, MioTheme.colors.onPrimary.copy(alpha = 0.1f))
    } else null

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}