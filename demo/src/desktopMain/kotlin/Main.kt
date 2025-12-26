@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.miolib.ui.components.*
import com.miolib.ui.theme.*
import kotlinx.coroutines.launch
import screen.ComponentScreen
import screen.SmmsScreen

// 定义路由名称
object Routes {
    const val COMPONENTS = "components"
    const val SMMS = "smms"
}

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "MioLib Pro Demo v2.2 (Navigation)") {
            // --- 全局状态 ---
            var isDarkTheme by remember { mutableStateOf(false) }
            var useAndroidSize by remember { mutableStateOf(false) }
            var useSquareShape by remember { mutableStateOf(false) }

            val currentSizes = if (useAndroidSize) AndroidSizes else DesktopSizes
            val currentShapes = if (useSquareShape) SquareShapes else RoundedShapes

            // Navigation Controller
            val navController = rememberNavController()

            // Drawer & Snackbar 状态
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            // 记录当前路由用于更新 Drawer 选中状态
            var currentRoute by remember { mutableStateOf(Routes.COMPONENTS) }

            LaunchedEffect(navController) {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    destination.route?.let { currentRoute = it }
                }
            }

            MioTheme(
                darkTheme = isDarkTheme,
                sizes = currentSizes,
                shapes = currentShapes
            ) {
                MioDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        MioText("MioLib Menu", style = MioTheme.typography.titleLarge)
                        Spacer(Modifier.height(24.dp))

                        MioDrawerItem(
                            label = "组件展示",
                            selected = currentRoute == Routes.COMPONENTS,
                            onClick = {
                                navController.navigate(Routes.COMPONENTS) {
                                    popUpTo(Routes.COMPONENTS) { inclusive = true }
                                }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.Home, null) }
                        )

                        MioDrawerItem(
                            label = "网络测试 (SM.MS)",
                            selected = currentRoute == Routes.SMMS,
                            onClick = {
                                navController.navigate(Routes.SMMS) {
                                    popUpTo(Routes.COMPONENTS)
                                }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.Create, null) }
                        )
                    }
                ) {
                    MioScaffold(
                        snackbarHostState = snackbarHostState,
                        topBar = {
                            val title = when (currentRoute) {
                                Routes.COMPONENTS -> "MioLib 全家桶"
                                Routes.SMMS -> "SM.MS 图床测试"
                                else -> "MioLib"
                            }

                            MioTopBar(
                                title = title,
                                actions = {
                                    MioBadgeWrapper(count = null) {
                                        MioSwitch(checked = isDarkTheme, onCheckedChange = { isDarkTheme = it })
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, null, tint = MioTheme.colors.onBackground)
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = Routes.COMPONENTS,
                            modifier = Modifier.padding(padding)
                        ) {
                            composable(Routes.COMPONENTS) {
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

                            composable(Routes.SMMS) {
                                SmmsScreen(snackbarHostState)
                            }
                        }
                    }
                }
            }
        }
    }
}