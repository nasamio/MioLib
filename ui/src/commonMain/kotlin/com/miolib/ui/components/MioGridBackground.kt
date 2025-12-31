package com.miolib.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

/**
 * MioGridBackground: 动态网格背景容器
 *
 * @param isAnimated 是否开启平移动画，默认开启
 * @param gridSize 网格大小，默认 40.dp
 * @param lineWidth 网格线宽度，默认 1.dp
 * @param lineColor 网格线颜色，默认使用当前主题 Primary 颜色的低透明度版本
 * @param content 内容
 */
@Composable
fun MioGridBackground(
    modifier: Modifier = Modifier,
    isAnimated: Boolean = true,
    gridSize: Dp = 40.dp,
    lineWidth: Dp = 1.dp,
    lineColor: Color = MioTheme.colors.primary.copy(alpha = 0.08f), // 默认 8% 透明度
    content: @Composable BoxScope.() -> Unit
) {
    // 动画状态管理
    val infiniteTransition = rememberInfiniteTransition()
    
    // 进度：0f -> 1f，代表移动一个网格的距离
    // 使用 LinearEasing 实现匀速无限循环
    val progress by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing), // 3秒移动一格，速度适中
                repeatMode = RepeatMode.Restart
            )
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sizePx = gridSize.toPx()
            val widthPx = lineWidth.toPx()
            
            // 计算当前的偏移量 (向右下角移动)
            // 因为动画是 0->1 循环，视觉上会感觉一直在向右下飘动
            val offsetX = sizePx * progress
            val offsetY = sizePx * progress

            // 1. 绘制垂直线
            // 我们需要从负坐标开始绘制，以保证移动时边缘不会出现空白
            var x = (offsetX % sizePx) - sizePx
            while (x < size.width + sizePx) {
                drawLine(
                    color = lineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = widthPx
                )
                x += sizePx
            }

            // 2. 绘制水平线
            var y = (offsetY % sizePx) - sizePx
            while (y < size.height + sizePx) {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = widthPx
                )
                y += sizePx
            }
        }

        // 放置内容
        content()
    }
}