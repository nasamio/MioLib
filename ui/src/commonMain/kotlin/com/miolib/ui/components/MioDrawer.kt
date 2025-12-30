package com.miolib.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

/**
 * MioDrawer: 侧边抽屉导航容器
 *
 * @param drawerState 抽屉状态
 * @param modifier 应用于最外层容器的修饰符
 * @param sheetModifier 应用于侧边栏(Sheet)的修饰符，默认宽度为 300.dp，可从外部覆盖
 * @param drawerContent 抽屉内的菜单内容
 * @param content 主页面内容
 */
@Composable
fun MioDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    // 将宽度提取为参数，并给定默认值
    sheetModifier: Modifier = Modifier.width(300.dp),
    drawerContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MioTheme.colors.surface,
                drawerContentColor = MioTheme.colors.onSurface,
                modifier = sheetModifier // 这里应用传入的修饰符
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    drawerContent()
                }
            }
        },
        content = content
    )
}

/**
 * MioDrawerItem: 抽屉菜单项
 */
@Composable
fun MioDrawerItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    NavigationDrawerItem(
        label = { MioText(label, style = MioTheme.typography.body) },
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MioTheme.colors.primary.copy(alpha = 0.1f),
            selectedTextColor = MioTheme.colors.primary,
            selectedIconColor = MioTheme.colors.primary,
            unselectedTextColor = MioTheme.colors.onSurface,
            unselectedIconColor = MioTheme.colors.outline
        ),
        shape = MioTheme.shapes.cornerMedium
    )
}