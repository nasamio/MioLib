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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.miolib.ui.theme.MioTheme

/**
 * MioImage: 支持点击放大的图片组件
 *
 * 修复：现在会自动适配黑夜模式下的图标颜色。
 *
 * @param tint 强制指定图片的着色 (通常用于 Icon)。如果不传：
 * - 对于 ImageVector：小图模式自动跟随 Theme.onSurface (黑/白)，全屏模式自动变白。
 * - 对于 Painter (照片)：默认显示原色，不着色。
 */
@Composable
fun MioImage(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    contentDescription: String? = null,
    size: Dp = 100.dp,
    cornerRadius: Dp = 8.dp,
    contentScale: ContentScale = ContentScale.Crop,
    enableClickToExpand: Boolean = true,
    tint: Color? = null // 新增：手动控制颜色
) {
    // 是否处于全屏查看模式
    var isExpanded by remember { mutableStateOf(false) }

    // 计算小图模式下的默认颜色过滤器
    val defaultColorFilter = if (tint != null) {
        ColorFilter.tint(tint)
    } else if (imageVector != null) {
        // 关键修复：如果是矢量图，默认跟随当前主题的文字颜色 (OnSurface)
        // 这样在深色模式下会自动变白，浅色模式下变黑
        ColorFilter.tint(MioTheme.colors.onSurface)
    } else {
        // 如果是照片 (Painter)，默认不加滤镜显示原色
        null
    }

    // --- 小图模式 (正常显示) ---
    Box(
        modifier = modifier
            .size(size)
            .clip(MioTheme.shapes.cornerMedium) // 使用主题圆角
            .background(MioTheme.colors.surface) // 背景色跟随主题
            .clickable(enabled = enableClickToExpand) { isExpanded = true },
        contentAlignment = Alignment.Center
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                colorFilter = defaultColorFilter // 支持手动 tint
            )
        } else if (imageVector != null) {
            Image(
                imageVector = imageVector,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                colorFilter = defaultColorFilter // 自动适配深色模式
            )
        }
    }

    // --- 全屏模式 (Popup 覆盖) ---
    if (isExpanded) {
        MioImageFullScreenViewer(
            imageVector = imageVector,
            painter = painter,
            contentDescription = contentDescription,
            originalTint = tint, // 传递手动设置的 tint
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
    originalTint: Color?,
    onDismiss: () -> Unit
) {
    // 缩放比例
    var scale by remember { mutableStateOf(1f) }
    // 偏移量 (拖拽)
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // 动画缩放值
    val animatedScale by animateFloatAsState(targetValue = scale)

    // 计算全屏模式下的颜色过滤器
    val fullScreenColorFilter = if (originalTint != null) {
        ColorFilter.tint(originalTint) // 如果用户指定了颜色，保持不变
    } else if (imageVector != null) {
        // 关键修复：全屏背景是黑色的，所以矢量图强制变白，否则看不见
        ColorFilter.tint(Color.White)
    } else {
        null // 照片保持原色
    }

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss
    ) {
        // 全屏黑色背景容器 (Lightbox 效果)
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f)) // 加深背景不透明度
                    // 1. 处理手势：缩放和拖拽
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                val maxTranslateX = (1000f * (scale - 1)) / 2 // 估算边界，简化处理
                                val maxTranslateY = (1000f * (scale - 1)) / 2
                                offsetX = (offsetX + pan.x * scale).coerceIn(-maxTranslateX, maxTranslateX)
                                offsetY = (offsetY + pan.y * scale).coerceIn(-maxTranslateY, maxTranslateY)
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
                    // 2. 处理点击
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismiss() },
                            onDoubleTap = {
                                if (scale > 1f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                } else {
                                    scale = 2.5f
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // 显示图片
                val graphicModifier = Modifier
                    .graphicsLayer(
                        scaleX = animatedScale,
                        scaleY = animatedScale,
                        translationX = offsetX,
                        translationY = offsetY
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
                        modifier = graphicModifier.size(300.dp), // 矢量图全屏基准大小
                        contentScale = ContentScale.Fit,
                        colorFilter = fullScreenColorFilter // 强制变白
                    )
                }

                // 底部提示文字
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    MioText(
                        text = "双击缩放 / 单击关闭",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MioTheme.typography.caption
                    )
                }
            }
        }
    }
}