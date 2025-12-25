package com.miolib.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.miolib.ui.theme.MioTheme

/**
 * MioScaffold: 页面基础脚手架
 * 帮你自动处理 TopBar 的位置和背景色，是构建页面的容器。
 *
 * @param modifier 修饰符
 * @param topBar 顶部的组件，通常放入 MioTopBar
 * @param containerColor 整个页面的背景色，默认自动适配深色/浅色模式
 * @param content 页面内容区域，会传给你 paddingValues，记得用到你的内容容器上，防止被 TopBar 遮挡
 */
@Composable
fun MioScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    containerColor: Color = MioTheme.colors.background,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        containerColor = containerColor,
        content = content
    )
}