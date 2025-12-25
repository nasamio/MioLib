package com.miolib.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

/**
 * 线性进度条
 * @param progress 0.0 到 1.0，如果传 null 则为无限加载模式
 */
@Composable
fun MioLinearProgress(
    modifier: Modifier = Modifier,
    progress: Float? = null,
) {
    val trackColor = MioTheme.colors.primary.copy(alpha = 0.2f)
    val color = MioTheme.colors.primary

    if (progress != null) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = modifier,
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round // 圆头更精致
        )
    } else {
        LinearProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round
        )
    }
}

/**
 * 圆形加载圈
 */
@Composable
fun MioLoading(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp, // 默认大小
    strokeWidth: Dp = 3.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = MioTheme.colors.primary,
        strokeWidth = strokeWidth,
        trackColor = MioTheme.colors.primary.copy(alpha = 0.1f), // 加个淡淡的底圈更高级
        strokeCap = StrokeCap.Round
    )
}