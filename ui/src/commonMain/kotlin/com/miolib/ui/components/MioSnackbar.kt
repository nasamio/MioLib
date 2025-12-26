package com.miolib.ui.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.miolib.ui.theme.MioTheme

/**
 * MioSnackbar: 自定义样式的消息提示条
 * 通常不需要直接调用，而是通过 MioScaffold 自动渲染
 */
@Composable
fun MioSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = MioTheme.shapes.cornerMedium, // 使用我们的圆角
    containerColor: Color = MioTheme.colors.onSurface.copy(alpha = 0.9f), // 深色背景
    contentColor: Color = MioTheme.colors.surface, // 浅色文字
    actionColor: Color = MioTheme.colors.primary // 主色调按钮
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionColor
    )
}