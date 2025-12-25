package com.miolib.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.miolib.ui.theme.MioTheme

/**
 * MioTopBar: 通用顶部标题栏
 * 采用居中标题设计，左侧可选返回键，右侧可扩展操作按钮。
 *
 * @param title 中间显示的标题文字
 * @param modifier 外部修饰符
 * @param onBackClick 左侧返回按钮的点击回调。如果不传(null)，则不显示返回箭头。
 * @param actions 右侧的操作按钮区域（例如设置、保存按钮）。使用 RowScope，可以放多个 IconButton。
 * @param backgroundColor 背景颜色，默认跟随 Theme.background
 * @param contentColor 内容颜色（图标和文字），默认跟随 Theme.onBackground
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
    CenterAlignedTopAppBar(
        title = {
            MioText(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold, // 标题加粗更明显
                color = contentColor
            )
        },
        modifier = modifier,
        // 导航图标区域 (左侧)
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
        // 操作图标区域 (右侧)
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
            navigationIconContentColor = contentColor
        )
    )
}