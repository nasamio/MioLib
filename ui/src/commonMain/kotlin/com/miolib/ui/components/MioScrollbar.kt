package com.miolib.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * MioScrollbar: 跨平台滚动条封装
 *
 * - Desktop: 显示垂直滚动条
 * - Android/iOS: 不显示 (或由系统处理)
 */
@Composable
expect fun MioScrollbar(
    modifier: Modifier = Modifier,
    state: LazyListState
)