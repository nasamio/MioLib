@file:OptIn(ExperimentalMaterial3Api::class)

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
import com.miolib.ui.components.*
import com.miolib.ui.theme.*
import kotlinx.coroutines.launch
import screen.ComponentScreen
import screen.SmmsScreen

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "MioLib Storage Manager") {
            // --- 全局状态 ---
            var isDarkTheme by remember { mutableStateOf(false) }
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
                darkTheme = isDarkTheme,
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
                                // [Update] 使用 MioIcon
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
                                        // [Update] 使用 MioIcon
                                        MioIcon(
                                            Icons.Default.Menu,
                                            null,
                                            tint = MioTheme.colors.onBackground
                                        )
                                    }
                                },
                                actions = {
                                    MioSwitch(
                                        checked = isDarkTheme,
                                        onCheckedChange = { isDarkTheme = it }
                                    )
                                }
                            )
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = Routes.COMPONENTS.route,
                            modifier = Modifier.padding(padding)
                        ) {
                            composable(Routes.COMPONENTS.route) {
                                ComponentScreen(
                                    snackbarHostState = snackbarHostState,
                                    useAndroidSize = useAndroidSize,
                                    onUseAndroidSizeChange = { useAndroidSize = it },
                                    useSquareShape = useSquareShape,
                                    onUseSquareShapeChange = { useSquareShape = it },
                                    isDarkTheme = isDarkTheme,
                                    onThemeChange = { isDarkTheme = it }
                                )
                            }

                            composable(Routes.SMMS.route) {
                                SmmsScreen(snackbarHostState)
                            }
                        }
                    }
                }
            }
        }
    }
}