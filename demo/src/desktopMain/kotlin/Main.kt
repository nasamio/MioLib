@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.miolib.ui.components.*
import com.miolib.ui.theme.*
import kotlinx.coroutines.launch

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "MioLib 全组件终极测试 v2.0") {

        // --- 全局配置与状态 ---
        var isDarkTheme by remember { mutableStateOf(false) }
        var useAndroidSize by remember { mutableStateOf(false) }
        var useSquareShape by remember { mutableStateOf(false) }

        // 核心控制器
        val snackbarHostState = remember { SnackbarHostState() }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        // --- 交互组件状态 ---
        var inputText by remember { mutableStateOf("") }
        var isSwitchOn by remember { mutableStateOf(true) }
        var isChecked by remember { mutableStateOf(false) }
        var radioOption by remember { mutableStateOf(0) }
        var sliderValue by remember { mutableStateOf(0.5f) }
        var clickCount by remember { mutableStateOf(0) }

        // --- 导航与弹窗状态 ---
        var showDialog by remember { mutableStateOf(false) }
        var showBottomSheet by remember { mutableStateOf(false) }
        var topTabIndex by remember { mutableStateOf(0) }
        var bottomTabIndex by remember { mutableStateOf(0) }
        var sideTabIndex by remember { mutableStateOf(0) }
        var selectedDrawerItem by remember { mutableStateOf(0) }

        // --- 动态主题配置 ---
        val currentSizes = if (useAndroidSize) AndroidSizes else DesktopSizes
        val currentShapes = if (useSquareShape) SquareShapes else RoundedShapes

        // --- 模拟数据 ---
        val tabItems = listOf(
            MioTabItem("首页", Icons.Default.Home),
            MioTabItem("发现", Icons.Default.Search),
            MioTabItem("设置", Icons.Default.Settings)
        )

        MioTheme(
            darkTheme = isDarkTheme,
            sizes = currentSizes,
            shapes = currentShapes
        ) {
            // 1. 最外层：侧边抽屉 (Drawer)
            MioDrawer(
                drawerState = drawerState,
                drawerContent = {
                    MioText("MioLib Menu", style = MioTheme.typography.titleLarge)
                    Spacer(Modifier.height(24.dp))
                    MioDrawerItem(
                        label = "组件展示",
                        selected = selectedDrawerItem == 0,
                        onClick = {
                            selectedDrawerItem = 0
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Home, null) }
                    )
                    MioDrawerItem(
                        label = "使用文档",
                        selected = selectedDrawerItem == 1,
                        onClick = {
                            selectedDrawerItem = 1
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Info, null) }
                    )
                }
            ) {
                // 2. 主体骨架：Scaffold
                MioScaffold(
                    snackbarHostState = snackbarHostState,
                    topBar = {
                        MioTopBar(
                            title = "MioLib 全家桶",
                            onBackClick = { scope.launch { drawerState.open() } }, // 演示打开抽屉
                            actions = {
                                // 徽标演示：带数字
                                MioBadgeWrapper(count = 3) {
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Default.Notifications, null, tint = MioTheme.colors.onBackground)
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                // 徽标演示：红点
                                MioBadgeWrapper(count = null) {
                                    MioSwitch(checked = isDarkTheme, onCheckedChange = { isDarkTheme = it })
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                        )
                    }
                ) { padding ->
                    // 3. 内容区域：垂直滚动列表
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

                        // --- Part 0: 全局样式控制 ---
                        MioCard {
                            MioText("0. 样式控制台", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MioSwitch(checked = useAndroidSize, onCheckedChange = { useAndroidSize = it })
                                Spacer(Modifier.width(8.dp))
                                MioText("移动端尺寸", style = MioTheme.typography.body)

                                Spacer(Modifier.width(24.dp))

                                MioSwitch(checked = useSquareShape, onCheckedChange = { useSquareShape = it })
                                Spacer(Modifier.width(8.dp))
                                MioText("直角风格", style = MioTheme.typography.body)
                            }
                        }

                        // --- Part 1: 排版 (Typography) ---
                        MioCard {
                            MioText("1. 排版系统 (Typography)", style = MioTheme.typography.titleMedium)
                            HorizontalDivider(
                                Modifier.padding(vertical = 8.dp),
                                color = MioTheme.colors.outline.copy(alpha = 0.2f)
                            )
                            MioText("Display Title", style = MioTheme.typography.display)
                            MioText("Title Large", style = MioTheme.typography.titleLarge)
                            MioText("Title Medium", style = MioTheme.typography.titleMedium)
                            MioText("Body Text: 正文内容，清晰易读。", style = MioTheme.typography.body)
                            MioText("Label Text", style = MioTheme.typography.label)
                            MioText(
                                "Caption Text: 辅助说明文字",
                                style = MioTheme.typography.caption,
                                color = MioTheme.colors.outline
                            )
                        }

                        // --- Part 2: 按钮 (Button & Toast) ---
                        MioCard {
                            MioText("2. 按钮与反馈", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MioButton("Small", size = MioSize.Small, onClick = {})
                                Spacer(Modifier.width(8.dp))
                                MioButton("Medium", size = MioSize.Medium, onClick = {})
                                Spacer(Modifier.width(8.dp))
                                MioButton("Large", size = MioSize.Large, onClick = {})
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MioButton("禁用按钮", enabled = false, onClick = {})
                                Spacer(Modifier.width(8.dp))
                                MioButton("显示 Toast", backgroundColor = MioTheme.colors.outline, onClick = {
                                    scope.launch { snackbarHostState.showSnackbar("这是一个 Mio 风格的消息提示！") }
                                })
                            }
                        }

                        // --- Part 3: 输入框 (Input) ---
                        MioCard {
                            MioText("3. 输入框 (Input)", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            MioInput(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = "Small Input",
                                size = MioSize.Small
                            )
                            Spacer(Modifier.height(8.dp))
                            MioInput(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = "Medium Input (Default)",
                                size = MioSize.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            MioInput(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = "Large Input",
                                size = MioSize.Large
                            )
                        }

                        // --- Part 4: 标签、头像与徽标 (Tag, Avatar, Badge) ---
                        MioCard {
                            MioText("4. 标签、头像与徽标", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MioBadgeWrapper(count = 1) {
                                    MioAvatar("A", size = 48.dp)
                                }
                                Spacer(Modifier.width(16.dp))
                                MioBadgeWrapper(count = 99) {
                                    MioAvatar("B", size = 32.dp, backgroundColor = MioTheme.colors.outline)
                                }
                                Spacer(Modifier.width(24.dp))
                                MioTag("Primary Tag")
                                Spacer(Modifier.width(8.dp))
                                MioTag(
                                    "Tag 2",
                                    backgroundColor = MioTheme.colors.outline.copy(alpha = 0.2f),
                                    textColor = MioTheme.colors.onSurface
                                )
                            }
                        }

                        // --- Part 5: 选择与滑动 (Selection & Slider) ---
                        MioCard {
                            MioText("5. 选择与滑动", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))

                            // Switch
                            MioListItem(
                                headline = "Switch",
                                trailingContent = {
                                    MioSwitch(
                                        checked = isSwitchOn,
                                        onCheckedChange = { isSwitchOn = it })
                                })
                            HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.1f))

                            // Checkbox
                            MioListItem(
                                headline = "CheckBox",
                                trailingContent = {
                                    MioCheckBox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it })
                                },
                                onClick = { isChecked = !isChecked }
                            )
                            HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.1f))

                            // Radio
                            MioListItem(
                                headline = "Radio Group",
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        MioRadioButton(selected = radioOption == 0, onClick = { radioOption = 0 })
                                        MioText("A", style = MioTheme.typography.body)
                                        Spacer(Modifier.width(8.dp))
                                        MioRadioButton(selected = radioOption == 1, onClick = { radioOption = 1 })
                                        MioText("B", style = MioTheme.typography.body)
                                    }
                                }
                            )
                            HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.1f))

                            // Slider
                            Column(Modifier.padding(top = 8.dp)) {
                                MioText(
                                    "Slider Value: ${(sliderValue * 100).toInt()}%",
                                    style = MioTheme.typography.caption
                                )
                                MioSlider(value = sliderValue, onValueChange = { sliderValue = it })
                            }
                        }

                        // --- Part 6: 进度条 (Progress) ---
                        MioCard {
                            MioText("6. 状态指示", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MioLoading(size = 24.dp)
                                Spacer(Modifier.width(16.dp))
                                MioLinearProgress(progress = 0.7f, modifier = Modifier.weight(1f))
                                Spacer(Modifier.width(8.dp))
                                MioText("70%", style = MioTheme.typography.caption)
                            }
                            Spacer(Modifier.height(8.dp))
                            MioLinearProgress(modifier = Modifier.fillMaxWidth()) // 无限加载
                        }

                        // --- Part 7: 弹窗与列表 (Dialog, BottomSheet, ListItem) ---
                        MioCard(onClick = { clickCount++ }) {
                            MioListItem(
                                headline = "7. 交互列表项",
                                supportText = "点击测试水波纹与计数: $clickCount",
                                leadingContent = {
                                    Icon(
                                        Icons.Default.AddCircle,
                                        null,
                                        tint = MioTheme.colors.primary
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        null,
                                        tint = MioTheme.colors.outline
                                    )
                                }
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            MioButton("打开 Dialog", modifier = Modifier.weight(1f), onClick = { showDialog = true })
                            MioButton(
                                "打开 BottomSheet",
                                modifier = Modifier.weight(1f),
                                backgroundColor = MioTheme.colors.surface,
                                contentColor = MioTheme.colors.primary,
                                onClick = { showBottomSheet = true })
                        }

                        // --- Part 8: 导航 Tab (Top & Bottom) ---
                        MioCard {
                            MioText("8. 顶部与底部导航", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            MioTabBar(
                                items = tabItems,
                                selectedIndex = topTabIndex,
                                onTabSelected = { topTabIndex = it },
                                type = MioTabType.Top
                            )
                            Spacer(Modifier.height(16.dp))
                            MioTabBar(
                                items = tabItems,
                                selectedIndex = bottomTabIndex,
                                onTabSelected = { bottomTabIndex = it },
                                type = MioTabType.Bottom
                            )
                        }

                        // --- Part 9: 侧边导航 (Side) ---
                        MioCard {
                            MioText("9. 侧边导航 (Side Navigation)", style = MioTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                                MioTabBar(
                                    items = tabItems,
                                    selectedIndex = sideTabIndex,
                                    onTabSelected = { sideTabIndex = it },
                                    type = MioTabType.Side
                                )
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    MioText(
                                        "Content Area",
                                        style = MioTheme.typography.display,
                                        color = MioTheme.colors.outline.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }

                        // --- Part 10: 图片查看器 (Image Viewer) ---
                        MioCard {
                            MioText("10. 图片查看器 (Lightbox)", style = MioTheme.typography.titleMedium)
                            MioText(
                                "点击图片全屏查看，全屏下支持双击缩放和拖拽。",
                                style = MioTheme.typography.caption,
                                color = MioTheme.colors.outline
                            )
                            Spacer(Modifier.height(16.dp))
                            Row {
                                MioImage(imageVector = Icons.Default.Face, size = 80.dp, contentDescription = "Face")
                                Spacer(Modifier.width(16.dp))
                                MioImage(
                                    imageVector = Icons.Default.LocationOn,
                                    size = 80.dp,
                                    contentDescription = "Landscape",
                                    cornerRadius = 40.dp
                                )
                            }
                        }
                    }
                }

                // --- 4. 弹窗层级 (Dialog & BottomSheet) ---

                // Dialog
                if (showDialog) {
                    MioDialog(
                        onDismissRequest = { showDialog = false },
                        title = "Mio Dialog",
                        text = "这是一个标准的对话框组件，适配了当前的圆角和字体设置。",
                        confirmButtonText = "确定",
                        onConfirm = { showDialog = false },
                        dismissButtonText = "取消",
                        onDismiss = { showDialog = false }
                    )
                }

                // BottomSheet
                if (showBottomSheet) {
                    MioBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MioText("Mio BottomSheet", style = MioTheme.typography.titleLarge)
                            Spacer(Modifier.height(16.dp))
                            MioText(
                                "这是一个底部弹窗，常用于移动端菜单或详情展示。",
                                style = MioTheme.typography.body,
                                color = MioTheme.colors.outline
                            )
                            Spacer(Modifier.height(32.dp))
                            MioButton("关闭", modifier = Modifier.fillMaxWidth(), onClick = { showBottomSheet = false })
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}