package com.miolib.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.miolib.ui.theme.MioTheme

/**
 * MioScaffold: 页面基础脚手架 (更新版)
 * 新增 snackbarHostState 参数，用于支持全局消息提示
 */
@Composable
fun MioScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null, // 新增：传入 State 以支持 Toast
    containerColor: Color = MioTheme.colors.background,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        containerColor = containerColor,
        snackbarHost = {
            if (snackbarHostState != null) {
                // 使用我们自定义样式的 SnackbarHost
                SnackbarHost(hostState = snackbarHostState) { data ->
                    MioSnackbar(data)
                }
            }
        },
        content = content
    )
}