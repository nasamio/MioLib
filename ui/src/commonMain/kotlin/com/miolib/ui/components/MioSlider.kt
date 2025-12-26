package com.miolib.ui.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.miolib.ui.theme.MioTheme

/**
 * MioSlider: 风格统一的滑动条
 */
@Composable
fun MioSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            thumbColor = MioTheme.colors.primary,
            activeTrackColor = MioTheme.colors.primary,
            inactiveTrackColor = MioTheme.colors.outline.copy(alpha = 0.2f)
        )
    )
}