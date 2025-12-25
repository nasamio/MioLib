package com.miolib.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.miolib.ui.theme.MioTheme

@Composable
fun MioListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supportText: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val finalModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    ListItem(
        headlineContent = {
            // 修改：使用 Body (加粗) 或 TitleMedium
            MioText(
                text = headline,
                style = MioTheme.typography.body,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = if (supportText != null) {
            {
                // 修改：使用 Caption
                MioText(
                    text = supportText,
                    style = MioTheme.typography.caption,
                    color = MioTheme.colors.outline
                )
            }
        } else null,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        modifier = finalModifier,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = MioTheme.colors.onSurface,
            leadingIconColor = MioTheme.colors.onSurface,
            trailingIconColor = MioTheme.colors.outline
        )
    )
}