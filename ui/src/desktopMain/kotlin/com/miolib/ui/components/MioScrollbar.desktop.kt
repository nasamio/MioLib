package com.miolib.ui.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.miolib.ui.theme.MioTheme

@Composable
actual fun MioScrollbar(
    modifier: Modifier,
    state: LazyListState
) {
    // 使用 Compose Desktop 的垂直滚动条
    VerticalScrollbar(
        modifier = modifier.fillMaxHeight(),
        adapter = rememberScrollbarAdapter(state),
        style = androidx.compose.foundation.defaultScrollbarStyle().copy(
            unhoverColor = MioTheme.colors.outline.copy(alpha = 0.3f),
            hoverColor = MioTheme.colors.outline.copy(alpha = 0.6f)
        )
    )
}