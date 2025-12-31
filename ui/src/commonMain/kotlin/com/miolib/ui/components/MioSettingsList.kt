package com.miolib.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * MioSettingsList: 通用设置列表容器
 *
 * 特性：
 * 1. [修改] 去除了橡皮筋回弹效果
 * 2. [新增] 桌面端自动显示右侧滚动条 (通过 MioScrollbar)
 * 3. 统一的内容内边距
 */
@Composable
fun MioSettingsList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1. 列表内容
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            contentPadding = contentPadding
        ) {
            content()
        }

        // 2. 跨平台滚动条 (悬浮在右侧)
        // 在 Android 上它是空的，在 Desktop 上它会显示
        MioScrollbar(
            state = state,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}