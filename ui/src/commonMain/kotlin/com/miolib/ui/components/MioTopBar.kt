package com.miolib.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.miolib.ui.theme.MioTheme

/**
 * MioTopBar: 通用顶部标题栏
 *
 * 修复了深浅模式切换时的颜色延迟问题。
 * 原理：将 TopAppBar 内部背景设为透明，由外层 Surface 接管背景渲染，
 * 从而避开 Material3 原生组件内部的颜色过渡动画。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MioTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    // 如果传入此参数，将覆盖默认的返回箭头逻辑
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MioTheme.colors.background,
    contentColor: Color = MioTheme.colors.onBackground
) {
    Surface(
        color = backgroundColor,
        modifier = modifier
    ) {
        CenterAlignedTopAppBar(
            title = {
                MioText(
                    text = title,
                    style = MioTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            },
            // 这里的 modifier 不再需要背景色，因为外层 Surface 已经处理了
            modifier = Modifier,
            navigationIcon = {
                // 优先渲染自定义的 navigationIcon
                if (navigationIcon != null) {
                    navigationIcon()
                }
                // 如果没有自定义 icon，但有点击事件，则显示默认返回箭头
                else if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = contentColor
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,

                titleContentColor = contentColor,
                actionIconContentColor = contentColor,
                navigationIconContentColor = contentColor
            )
        )
    }
}