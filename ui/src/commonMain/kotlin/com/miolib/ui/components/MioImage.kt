package com.miolib.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.miolib.ui.theme.MioTheme
import com.miolib.ui.theme.MioThemeStyle

/**
 * MioImage: 通用图片组件 (增强版)
 *
 * 既可以作为普通 Image 使用，也支持圆角裁切和点击全屏查看。
 *
 * @param enableClickToExpand 是否开启点击全屏查看 (默认 false)
 * @param cornerRadius 圆角大小 (默认 0.dp，即直角)
 */
@Composable
fun MioImage(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    imageVector: ImageVector? = null,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    // --- 增强功能 ---
    enableClickToExpand: Boolean = false,
    cornerRadius: Dp = 0.dp
) {
    // 是否处于全屏查看模式
    var isExpanded by remember { mutableStateOf(false) }

    // 处理圆角和点击
    val finalModifier = modifier
        .let {
            if (cornerRadius > 0.dp) it.clip(RoundedCornerShape(cornerRadius)) else it
        }
        .let {
            if (enableClickToExpand) it.clickable { isExpanded = true } else it
        }

    // 智能计算 ColorFilter: 如果是 Vector 且未指定 Filter，则适配主题色
    val finalColorFilter = if (colorFilter != null) {
        colorFilter
    } else if (imageVector != null) {
        // Vector 默认跟随文字颜色
        ColorFilter.tint(MioTheme.colors.onSurface)
    } else {
        null
    }

    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = finalModifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter // Painter 默认不加 Tint，除非外部指定
        )
    } else if (imageVector != null) {
        Image(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = finalModifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = finalColorFilter
        )
    }

    // --- 全屏模式 (Popup 覆盖) ---
    if (isExpanded) {
        MioImageFullScreenViewer(
            imageVector = imageVector,
            painter = painter,
            contentDescription = contentDescription,
            originalColorFilter = colorFilter,
            onDismiss = { isExpanded = false }
        )
    }
}

/**
 * 全屏查看器内部实现
 */
@Composable
private fun MioImageFullScreenViewer(
    imageVector: ImageVector?,
    painter: Painter?,
    contentDescription: String?,
    originalColorFilter: ColorFilter?,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val animatedScale by animateFloatAsState(targetValue = scale)

    // 全屏时，矢量图强制变白，除非原图有滤镜
    val fullScreenColorFilter = if (originalColorFilter != null) {
        originalColorFilter
    } else if (imageVector != null) {
        ColorFilter.tint(Color.White)
    } else {
        null
    }

    Popup(alignment = Alignment.Center, onDismissRequest = onDismiss) {
        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                val maxTx = (1000f * (scale - 1)) / 2
                                val maxTy = (1000f * (scale - 1)) / 2
                                offsetX = (offsetX + pan.x * scale).coerceIn(-maxTx, maxTx)
                                offsetY = (offsetY + pan.y * scale).coerceIn(-maxTy, maxTy)
                            } else {
                                offsetX = 0f; offsetY = 0f
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismiss() },
                            onDoubleTap = {
                                if (scale > 1f) { scale = 1f; offsetX = 0f; offsetY = 0f } else { scale = 2.5f }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val graphicModifier = Modifier.graphicsLayer(
                    scaleX = animatedScale, scaleY = animatedScale,
                    translationX = offsetX, translationY = offsetY
                )

                if (painter != null) {
                    Image(
                        painter = painter,
                        contentDescription = contentDescription,
                        modifier = graphicModifier,
                        contentScale = ContentScale.Fit,
                        colorFilter = fullScreenColorFilter
                    )
                } else if (imageVector != null) {
                    Image(
                        imageVector = imageVector,
                        contentDescription = contentDescription,
                        modifier = graphicModifier.size(300.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = fullScreenColorFilter
                    )
                }

                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)) {
                    // [修复] 使用新的 MioThemeStyle.Dark 替代已移除的 darkTheme=true 参数
                    MioTheme(style = MioThemeStyle.Dark) {
                        androidx.compose.material3.Text(
                            text = "双击缩放 / 单击关闭",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MioTheme.typography.caption
                        )
                    }
                }
            }
        }
    }
}