package com.miolib.demo

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.miolib.demo.screen.*
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import com.miolib.ui.theme.state.*
import kotlinx.coroutines.launch
import com.miolib.demo.screen.ComponentScreen
import com.miolib.demo.screen.RssScreen
import com.miolib.demo.screen.SettingsScreen
import com.miolib.demo.screen.SmmsScreen

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "MioLib Storage Manager") {
            // --- 全局状态 ---
            //  统一使用 MioThemeStyle 管理所有主题状态 (包含颜色和深浅)
            var currentStyle by remember { mutableStateOf(MioThemeStyle.Light) }

            var useAndroidSize by remember { mutableStateOf(false) }
            var useSquareShape by remember { mutableStateOf(false) }

            // --- 样式计算 ---
            val currentSizes = if (useAndroidSize) AndroidSizes else DesktopSizes
            val currentShapes = if (useSquareShape) SquareShapes else RoundedShapes

            // --- 导航控制器与状态 ---
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRouteString = navBackStackEntry?.destination?.route
            val currentRouteObj = remember(currentRouteString) {
                Routes.getByRoute(currentRouteString)
            }

            MioTheme(
                // [修改] 传入当前选中的主题风格
                style = currentStyle,
                sizes = currentSizes,
                shapes = currentShapes
            ) {
                MioDrawer(
                    drawerState = drawerState,
                    sheetModifier = Modifier.width(200.dp),
                    drawerContent = {
                        MioText("MioLib Menu", style = MioTheme.typography.titleLarge)
                        Spacer(Modifier.height(24.dp))

                        Routes.all.forEach { item ->
                            MioDrawerItem(
                                label = item.title,
                                selected = currentRouteObj == item,
                                icon = { MioIcon(item.icon, contentDescription = null) },
                                onClick = {
                                    if (currentRouteObj != item) {
                                        navController.navigate(item.route) {
                                            val startRoute = navController.graph.findStartDestination().route
                                            if (startRoute != null) {
                                                popUpTo(startRoute) { saveState = true }
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    scope.launch { drawerState.close() }
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                ) {
                    MioScaffold(
                        snackbarHostState = snackbarHostState,
                        topBar = {
                            MioTopBar(
                                title = currentRouteObj?.title ?: "MioLib",
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        MioIcon(
                                            Icons.Default.Menu,
                                            null,
                                            tint = MioTheme.colors.onBackground
                                        )
                                    }
                                },
                                actions = {
                                    // [可选] 顶部栏依然可以放一个快捷切换深浅模式的开关，
                                    // 这里简单实现：如果在浅色系，切到默认深色；如果在深色系，切到默认浅色
                                    MioSwitch(
                                        checked = currentStyle.isDark,
                                        onCheckedChange = { isDark ->
                                            currentStyle = if (isDark) MioThemeStyle.Dark else MioThemeStyle.Light
                                        }
                                    )
                                }
                            )
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = Routes.all.minBy { it.index }.route,
                            modifier = Modifier.padding(padding)
                        ) {
                            composable(Routes.RSS.route) {
                                RssScreen(snackbarHostState)
                            }

                            composable(Routes.COMPONENTS.route) {
                                ComponentScreen(
                                    snackbarHostState = snackbarHostState,
                                    useAndroidSize = useAndroidSize,
                                    onUseAndroidSizeChange = { useAndroidSize = it },
                                    useSquareShape = useSquareShape,
                                    onUseSquareShapeChange = { useSquareShape = it },
                                    isDarkTheme = currentStyle.isDark, // 兼容旧组件参数
                                    onThemeChange = { isDark ->
                                        currentStyle = if (isDark) MioThemeStyle.Dark else MioThemeStyle.Light
                                    }
                                )
                            }

                            composable(Routes.SMMS.route) {
                                SmmsScreen(snackbarHostState)
                            }

                            composable(Routes.SETTINGS.route) {
                                SettingsScreen(
                                    snackbarHostState = snackbarHostState,
                                    currentStyle = currentStyle,
                                    onStyleChange = { currentStyle = it }
                                )
                            }

                            composable(Routes.R2.route) {
                                R2Screen(snackbarHostState)
                            }
                        }
                    }
                }
            }
        }
    }
}