package com.miolib.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme
import com.miolib.ui.theme.state.MioSize

/**
 * 下载状态密封接口
 */
sealed interface DownloadState {
    data object Idle : DownloadState
    data class Downloading(val progress: Float) : DownloadState // 0.0f - 1.0f
    data object Paused : DownloadState
    data object Success : DownloadState
    data object Error : DownloadState
}

/**
 * MioDownloadButton: 多状态下载按钮
 * [Fix] 修复了进度更新时文字闪烁的问题 (通过 contentKey)
 */
@Composable
fun MioDownloadButton(
    state: DownloadState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: MioSize = MioSize.Medium,
    idleText: String = "下载",
    successText: String = "打开",
    errorText: String = "重试",
) {
    // 1. 获取尺寸与形状规范
    val sizeSpec = when (size) {
        MioSize.Small -> MioTheme.sizes.small
        MioSize.Medium -> MioTheme.sizes.medium
        MioSize.Large -> MioTheme.sizes.large
    }
    val shape = MioTheme.shapes.cornerMedium

    // 2. 根据状态计算颜色
    val primaryColor = MioTheme.colors.primary
    val errorColor = Color.Red
    val successColor = Color(0xFF10B981) // 绿色

    val targetBackgroundColor = when (state) {
        DownloadState.Idle -> primaryColor
        is DownloadState.Downloading -> MioTheme.colors.surface
        DownloadState.Paused -> MioTheme.colors.surface
        DownloadState.Success -> successColor
        DownloadState.Error -> errorColor
    }

    val targetContentColor = when (state) {
        DownloadState.Idle -> MioTheme.colors.onPrimary
        is DownloadState.Downloading -> primaryColor
        DownloadState.Paused -> MioTheme.colors.outline
        DownloadState.Success -> Color.White
        DownloadState.Error -> Color.White
    }

    val borderColor = when (state) {
        is DownloadState.Downloading -> primaryColor.copy(alpha = 0.3f)
        DownloadState.Paused -> MioTheme.colors.outline.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    // 动画状态
    val animatedBgColor by animateColorAsState(targetBackgroundColor, animationSpec = tween(300))
    val animatedContentColor by animateColorAsState(targetContentColor, animationSpec = tween(300))
    val animatedProgress by animateFloatAsState(
        targetValue = if (state is DownloadState.Downloading) state.progress else 0f
    )

    // 3. 布局结构
    Box(
        modifier = modifier
            .height(sizeSpec.height)
            .width(if (size == MioSize.Small) 80.dp else 120.dp)
            .clip(shape)
            .background(animatedBgColor)
            .border(
                width = if (state is DownloadState.Downloading || state is DownloadState.Paused) 1.dp else 0.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White.copy(alpha = 0.3f))
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- 进度条层 (仅在下载或暂停时显示) ---
        if (state is DownloadState.Downloading || state is DownloadState.Paused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterStart)
            ) {
                // 进度填充
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(if (state is DownloadState.Downloading) animatedProgress else (state as? DownloadState.Downloading)?.progress ?: animatedProgress)
                        .background(primaryColor.copy(alpha = 0.15f))
                )
            }
        }

        // --- 内容层 (图标 + 文字) ---
        AnimatedContent(
            targetState = state,
            label = "DownloadButtonContent",
            contentKey = {
                if (it is DownloadState.Downloading) "Downloading" else it
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
            }
        ) { targetState ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                // 根据状态显示不同的内容
                when (targetState) {
                    DownloadState.Idle -> {
                        MioIcon(Icons.Default.KeyboardArrowDown, tint = animatedContentColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        MioText(idleText, color = animatedContentColor, fontSize = sizeSpec.fontSize, fontWeight = FontWeight.Bold)
                    }
                    is DownloadState.Downloading -> {
                        // 这里只是数据变了，不会触发 Fade 动画
                        MioText(
                            text = "${(targetState.progress * 100).toInt()}%",
                            color = animatedContentColor,
                            fontSize = sizeSpec.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    DownloadState.Paused -> {
                        MioIcon(Icons.Default.PlayArrow, tint = animatedContentColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        MioText("继续", color = animatedContentColor, fontSize = sizeSpec.fontSize, fontWeight = FontWeight.Bold)
                    }
                    DownloadState.Success -> {
                        MioIcon(Icons.Default.Check, tint = animatedContentColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        MioText(successText, color = animatedContentColor, fontSize = sizeSpec.fontSize, fontWeight = FontWeight.Bold)
                    }
                    DownloadState.Error -> {
                        MioIcon(Icons.Default.Refresh, tint = animatedContentColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        MioText(errorText, color = animatedContentColor, fontSize = sizeSpec.fontSize, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}