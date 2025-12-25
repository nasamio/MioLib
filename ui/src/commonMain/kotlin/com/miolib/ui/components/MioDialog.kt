package com.miolib.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.miolib.ui.theme.MioTheme

@Composable
fun MioDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String = "确定",
    onConfirm: () -> Unit,
    dismissButtonText: String? = "取消",
    onDismiss: (() -> Unit)? = null
) {
    // 引用 Theme 中的 Large 形状
    val shape = MioTheme.shapes.cornerLarge

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            // 修改：使用 TitleLarge
            MioText(text = title, style = MioTheme.typography.titleLarge)
        },
        text = {
            // 修改：使用 Body
            MioText(
                text = text,
                style = MioTheme.typography.body,
                color = MioTheme.colors.onSurface.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                // 按钮文字通常用 Label
                MioText(
                    text = confirmButtonText,
                    color = MioTheme.colors.primary,
                    style = MioTheme.typography.label,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            if (dismissButtonText != null) {
                TextButton(onClick = { onDismiss?.invoke() ?: onDismissRequest() }) {
                    MioText(
                        text = dismissButtonText,
                        color = MioTheme.colors.outline,
                        style = MioTheme.typography.label
                    )
                }
            }
        },
        containerColor = MioTheme.colors.surface,
        titleContentColor = MioTheme.colors.onSurface,
        textContentColor = MioTheme.colors.onSurface,
        shape = shape
    )
}