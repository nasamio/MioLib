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
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MioTheme.colors.background,
    contentColor: Color = MioTheme.colors.onBackground
) {
    // 关键修复：在外层套一个 Surface 来负责渲染背景色
    // Surface 的颜色切换是瞬间的，不会像 TopAppBar 内部那样有过渡动画
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
                if (onBackClick != null) {
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
                // 核心点：强制把组件内部的容器色设为透明
                // 这样用户看到的就是下方 Surface 的颜色，实现了瞬间切换
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,

                titleContentColor = contentColor,
                actionIconContentColor = contentColor,
                navigationIconContentColor = contentColor
            )
        )
    }
}