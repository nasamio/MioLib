package com.miolib.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.miolib.ui.theme.MioSize
import com.miolib.ui.theme.MioTheme

@Composable
fun MioInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: MioSize = MioSize.Medium, // 支持尺寸
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    // 1. 获取尺寸规范
    val sizeSpec = when (size) {
        MioSize.Small -> MioTheme.sizes.small
        MioSize.Medium -> MioTheme.sizes.medium
        MioSize.Large -> MioTheme.sizes.large
    }

    // 2. 获取形状规范
    val shape = MioTheme.shapes.cornerMedium

    val mioColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MioTheme.colors.primary,
        focusedLabelColor = MioTheme.colors.primary,
        cursorColor = MioTheme.colors.primary,
        unfocusedBorderColor = MioTheme.colors.outline.copy(alpha = 0.5f),
        unfocusedLabelColor = MioTheme.colors.onSurface.copy(alpha = 0.6f),
        focusedTextColor = MioTheme.colors.onSurface,
        unfocusedTextColor = MioTheme.colors.onSurface,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red
    )

    // 注意：OutlinedTextField 的高度调整比较 tricky，通常只需调整字体
    // 如果要强制高度，可能需要 BasicTextField 自定义，但这里我们通过 textStyle 来间接影响
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier, // 如果想强制高度，可以在这里加 .height(sizeSpec.height)
        label = label?.let { { Text(it, fontSize = sizeSpec.fontSize) } },
        placeholder = placeholder?.let { { Text(it, fontSize = sizeSpec.fontSize) } },
        isError = isError,
        singleLine = singleLine,
        shape = shape, // 动态圆角
        colors = mioColors,
        textStyle = TextStyle(fontSize = sizeSpec.fontSize), // 字体跟随尺寸
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}