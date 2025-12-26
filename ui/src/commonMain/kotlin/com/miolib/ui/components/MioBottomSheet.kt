package com.miolib.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.miolib.ui.theme.MioTheme

/**
 * MioBottomSheet: 底部弹窗容器
 *
 * @param onDismissRequest 点击外部或下滑关闭时的回调
 * @param sheetState 控制 Sheet 状态
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MioBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MioTheme.colors.surface,
        contentColor = MioTheme.colors.onSurface,
        shape = MioTheme.shapes.cornerLarge, // 顶部大圆角
        content = content
    )
}