package com.miolib.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

/**
 * MioTabItem: Tab 数据模型
 */
data class MioTabItem(
    val title: String,
    val icon: ImageVector? = null,
    val selectedIcon: ImageVector? = null // 可选：选中时的图标
)

/**
 * MioTabType: Tab 样式枚举
 */
enum class MioTabType {
    Top,    // 顶部 TabRow
    Bottom, // 底部 NavigationBar
    Side    // 侧边 NavigationRail
}

/**
 * MioTabBar: 统一 Tab 切换组件
 * 支持顶部、底部、侧边三种模式。
 *
 * @param items Tab 数据列表
 * @param selectedIndex 当前选中的索引
 * @param onTabSelected 选中回调
 * @param type Tab 类型 (Top, Bottom, Side)
 * @param modifier 修饰符
 */
@Composable
fun MioTabBar(
    items: List<MioTabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    type: MioTabType = MioTabType.Top
) {
    val containerColor = MioTheme.colors.surface
    val contentColor = MioTheme.colors.onSurface
    val primaryColor = MioTheme.colors.primary

    when (type) {
        // --- 1. 顶部 TabRow 样式 ---
        MioTabType.Top -> {
            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = modifier,
                containerColor = containerColor,
                contentColor = contentColor,
                indicator = { tabPositions ->
                    if (selectedIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                            color = primaryColor
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.2f))
                }
            ) {
                items.forEachIndexed { index, item ->
                    val selected = selectedIndex == index
                    Tab(
                        selected = selected,
                        onClick = { onTabSelected(index) },
                        text = {
                            MioText(
                                text = item.title,
                                style = MioTheme.typography.label,
                                color = if (selected) primaryColor else contentColor.copy(alpha = 0.7f),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        icon = if (item.icon != null) {
                            {
                                Icon(
                                    imageVector = if (selected && item.selectedIcon != null) item.selectedIcon else item.icon,
                                    contentDescription = item.title,
                                    tint = if (selected) primaryColor else contentColor.copy(alpha = 0.7f)
                                )
                            }
                        } else null
                    )
                }
            }
        }

        // --- 2. 底部 NavigationBar 样式 ---
        MioTabType.Bottom -> {
            NavigationBar(
                modifier = modifier,
                containerColor = containerColor,
                tonalElevation = 4.dp
            ) {
                items.forEachIndexed { index, item ->
                    val selected = selectedIndex == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onTabSelected(index) },
                        icon = {
                            if (item.icon != null) {
                                Icon(
                                    imageVector = if (selected && item.selectedIcon != null) item.selectedIcon else item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            MioText(
                                text = item.title,
                                style = MioTheme.typography.caption,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MioTheme.colors.onPrimary,
                            selectedTextColor = primaryColor,
                            indicatorColor = primaryColor,
                            unselectedIconColor = contentColor.copy(alpha = 0.6f),
                            unselectedTextColor = contentColor.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }

        // --- 3. 侧边 NavigationRail 样式 ---
        MioTabType.Side -> {
            NavigationRail(
                modifier = modifier,
                containerColor = containerColor,
                contentColor = contentColor
            ) {
                // 可以在这里加个 Header，比如 Menu 图标
                Spacer(Modifier.height(8.dp))
                items.forEachIndexed { index, item ->
                    val selected = selectedIndex == index
                    NavigationRailItem(
                        selected = selected,
                        onClick = { onTabSelected(index) },
                        icon = {
                            if (item.icon != null) {
                                Icon(
                                    imageVector = if (selected && item.selectedIcon != null) item.selectedIcon else item.icon,
                                    contentDescription = item.title
                                )
                            }
                        },
                        label = {
                            MioText(
                                text = item.title,
                                style = MioTheme.typography.caption
                            )
                        },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MioTheme.colors.onPrimary,
                            selectedTextColor = primaryColor,
                            indicatorColor = primaryColor,
                            unselectedIconColor = contentColor.copy(alpha = 0.6f),
                            unselectedTextColor = contentColor.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}