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

object Routes {
    const val COMPONENTS = "components"
    const val SMMS = "smms"
}

fun main() {
    application {
        // 调整窗口标题
        Window(onCloseRequest = ::exitApplication, title = "MioLib Storage Manager") {
            var isDarkTheme by remember { mutableStateOf(false) }
            // 默认使用直角风格和桌面尺寸，更像桌面软件
            var useAndroidSize by remember { mutableStateOf(false) }
            var useSquareShape by remember { mutableStateOf(true) }

            val currentSizes = if (useAndroidSize) AndroidSizes else DesktopSizes
            val currentShapes = if (useSquareShape) SquareShapes else RoundedShapes

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

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
                                if (currentRoute != Routes.COMPONENTS) navController.navigate(Routes.COMPONENTS) {
                                    popUpTo(Routes.SMMS)
                                }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.Home, null) }
                        )
                        Spacer(Modifier.height(12.dp))
                        MioDrawerItem(
                            label = "我的图床", // 调整顺序，图床在前
                            selected = currentRoute == Routes.SMMS,
                            onClick = {
                                if (currentRoute != Routes.SMMS) navController.navigate(Routes.SMMS) {
                                    popUpTo(Routes.SMMS) { inclusive = true }
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
                            // 当在图床页面时，TopBar 可以简化，把控制权交给页面内部，或者只保留基础功能
                            val title = when (currentRoute) {
                                Routes.COMPONENTS -> "组件预览"
                                Routes.SMMS -> "Mio 图床" // 简单标题
                                else -> "MioLib"
                            }

                            MioTopBar(
                                title = title,
                                actions = {
                                    MioSwitch(checked = isDarkTheme, onCheckedChange = { isDarkTheme = it })
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