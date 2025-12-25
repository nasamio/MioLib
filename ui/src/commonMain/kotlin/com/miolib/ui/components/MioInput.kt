package com.miolib.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

@Composable
fun MioInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    val mioColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MioTheme.colors.primary,
        focusedLabelColor = MioTheme.colors.primary,
        cursorColor = MioTheme.colors.primary,
        // 使用新的 outline 颜色，更加柔和
        unfocusedBorderColor = MioTheme.colors.outline.copy(alpha = 0.5f),
        unfocusedLabelColor = MioTheme.colors.onSurface.copy(alpha = 0.6f),
        focusedTextColor = MioTheme.colors.onSurface,
        unfocusedTextColor = MioTheme.colors.onSurface,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = mioColors,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}