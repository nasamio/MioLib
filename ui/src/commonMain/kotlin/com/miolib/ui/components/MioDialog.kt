package com.miolib.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.miolib.ui.theme.MioTheme
import kotlinx.coroutines.delay

/**
 * MioDialog 默认配置
 */
object MioDialogDefaults {
    // 动画时间
    const val EnterDuration = 400
    const val ExitDuration = 300

    // 默认进入动画：淡入 + 稍微明显的缩放弹簧
    val EnterTransition: EnterTransition = fadeIn(tween(EnterDuration)) +
            scaleIn(
                initialScale = 0.9f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, // 微弹
                    stiffness = Spring.StiffnessLow
                )
            )

    // 默认退出动画：淡出 + 缩小
    val ExitTransition: ExitTransition = fadeOut(tween(ExitDuration)) +
            scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(ExitDuration)
            )
}

/**
 * MioDialog (通用容器版)
 *
 * 修复逻辑：
 * 将动画状态 [animateIn] 移至 Popup 内部管理，并确保初始值为 false。
 * 这样当 Popup 挂载时，状态会从 false -> true，从而强制触发进场动画。
 */
@Composable
fun MioDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: PopupProperties = PopupProperties(
        focusable = true,
        dismissOnBackPress = true,
        dismissOnClickOutside = false
    ),
    enter: EnterTransition = MioDialogDefaults.EnterTransition,
    exit: ExitTransition = MioDialogDefaults.ExitTransition,
    content: @Composable () -> Unit
) {
    // 1. 控制 Popup 是否存在
    // 只有当 visible=true (需要显示) 或者 正在播放退出动画时，showPopup 才为 true
    var showPopup by remember { mutableStateOf(visible) }

    // 监听 visible 变化来控制 Popup 的销毁（延迟销毁以播放退出动画）
    LaunchedEffect(visible) {
        if (visible) {
            showPopup = true
        } else {
            // 关键：给退出动画留出足够的时间
            delay(MioDialogDefaults.ExitDuration.toLong())
            showPopup = false
        }
    }

    if (showPopup) {
        Popup(
            alignment = Alignment.Center,
            properties = properties,
            onDismissRequest = onDismissRequest
        ) {
            // 2. 控制动画播放状态 (关键修复)
            // 初始值必须是 false！这样 Popup 刚挂载时是不可见的
            var animateIn by remember { mutableStateOf(false) }

            // 监听 visible 变化来驱动动画
            LaunchedEffect(visible) {
                if (visible) {
                    // 这里不需要 delay，Compose 的状态快照机制会处理好
                    // 当 Popup 刚挂载(showPopup=true)时，visible 也是 true
                    // 这里的 animateIn 会从初始的 false 变为 true，触发进场动画
                    animateIn = true
                } else {
                    // visible 变 false，触发退出动画
                    animateIn = false
                }
            }

            // 全屏容器
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // --- A. 遮罩层 (Scrim) ---
                AnimatedVisibility(
                    visible = animateIn,
                    enter = fadeIn(tween(MioDialogDefaults.EnterDuration)),
                    exit = fadeOut(tween(MioDialogDefaults.ExitDuration))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onDismissRequest()
                            }
                    )
                }

                // --- B. 弹窗内容 ---
                AnimatedVisibility(
                    visible = animateIn,
                    enter = enter,
                    exit = exit,
                    modifier = modifier
                ) {
                    // 内容区域，点击不做任何事(拦截点击事件，防止点穿到遮罩)
                    Box(modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )) {
                        content()
                    }
                }
            }
        }
    }
}

/**
 * MioAlertDialog (标准对话框版)
 */
@Composable
fun MioAlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String = "确定",
    onConfirm: () -> Unit,
    dismissButtonText: String? = "取消",
    onDismiss: (() -> Unit)? = null
) {
    MioDialog(
        visible = visible,
        onDismissRequest = { onDismiss?.invoke() ?: onDismissRequest() }
    ) {
        MioCard(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 560.dp)
                .padding(24.dp),
            backgroundColor = MioTheme.colors.surface,
            elevation = 8.dp,
            onClick = null // 禁用 Card 自身的点击效果
        ) {
            Column {
                // 标题
                MioText(
                    text = title,
                    style = MioTheme.typography.titleLarge,
                    color = MioTheme.colors.onSurface
                )
                Spacer(Modifier.height(16.dp))

                // 内容
                MioText(
                    text = text,
                    style = MioTheme.typography.body,
                    color = MioTheme.colors.onSurface.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(24.dp))

                // 按钮组
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (dismissButtonText != null) {
                        TextButton(
                            onClick = { onDismiss?.invoke() ?: onDismissRequest() }
                        ) {
                            MioText(
                                text = dismissButtonText,
                                color = MioTheme.colors.outline,
                                style = MioTheme.typography.label
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }

                    TextButton(
                        onClick = { onConfirm() }
                    ) {
                        MioText(
                            text = confirmButtonText,
                            color = MioTheme.colors.primary,
                            style = MioTheme.typography.label,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}