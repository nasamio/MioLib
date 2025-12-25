package com.miolib.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.miolib.ui.theme.MioTheme

/**
 * MioSwitch: 风格统一的开关组件
 * 去除了点击时的光圈涟漪，视觉更简洁。
 */
@Composable
fun MioSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        thumbContent = null,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MioTheme.colors.primary,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = MioTheme.colors.outline,
            uncheckedTrackColor = MioTheme.colors.surface,
            uncheckedBorderColor = MioTheme.colors.outline.copy(alpha = 0.5f),
            uncheckedIconColor = Color.Transparent
        )
    )
}

/**
 * MioCheckBox: 风格统一的复选框
 */
@Composable
fun MioCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = MioTheme.colors.primary,
            uncheckedColor = MioTheme.colors.outline,
            checkmarkColor = Color.White
        )
    )
}

/**
 * MioRadioButton: 风格统一的单选框
 */
@Composable
fun MioRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = RadioButtonDefaults.colors(
            selectedColor = MioTheme.colors.primary,
            unselectedColor = MioTheme.colors.outline
        )
    )
}