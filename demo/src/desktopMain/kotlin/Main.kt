import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.miolib.ui.components.*
import com.miolib.ui.theme.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "MioLib 全组件终极测试") {

        // --- 控制台状态 ---
        var isDarkTheme by remember { mutableStateOf(false) }
        var useAndroidSize by remember { mutableStateOf(false) }
        var useSquareShape by remember { mutableStateOf(false) }

        // --- 组件交互状态 ---
        var inputText by remember { mutableStateOf("") }
        var isSwitchOn by remember { mutableStateOf(true) }
        var isChecked by remember { mutableStateOf(false) }
        var radioOption by remember { mutableStateOf(0) }
        var showDialog by remember { mutableStateOf(false) }
        var clickCount by remember { mutableStateOf(0) }

        val currentSizes = if (useAndroidSize) AndroidSizes else DesktopSizes
        val currentShapes = if (useSquareShape) SquareShapes else RoundedShapes

        MioTheme(
            darkTheme = isDarkTheme,
            sizes = currentSizes,
            shapes = currentShapes
        ) {
            MioScaffold(
                topBar = {
                    MioTopBar(
                        title = "组件全家桶",
                        actions = {
                            MioText("夜间", style = MioTheme.typography.caption)
                            Spacer(Modifier.width(8.dp))
                            MioSwitch(checked = isDarkTheme, onCheckedChange = { isDarkTheme = it })
                            Spacer(Modifier.width(16.dp))
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    // --- 0. 全局控制台 ---
                    MioCard {
                        MioText("样式控制台", style = MioTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MioSwitch(checked = useAndroidSize, onCheckedChange = { useAndroidSize = it })
                            Spacer(Modifier.width(8.dp))
                            MioText("安卓尺寸 (Touch)", style = MioTheme.typography.body)

                            Spacer(Modifier.width(24.dp))

                            MioSwitch(checked = useSquareShape, onCheckedChange = { useSquareShape = it })
                            Spacer(Modifier.width(8.dp))
                            MioText("直角风格 (Square)", style = MioTheme.typography.body)
                        }
                    }

                    // --- 1. Typography 排版 ---
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

                    // --- 2. 按钮与尺寸 (Button) ---
                    MioCard {
                        MioText("2. 按钮 (Sizes: S/M/L)", style = MioTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MioButton(text = "Small", size = MioSize.Small, onClick = {})
                            Spacer(Modifier.width(8.dp))
                            MioButton(text = "Medium", size = MioSize.Medium, onClick = {})
                            Spacer(Modifier.width(8.dp))
                            MioButton(text = "Large", size = MioSize.Large, onClick = {})
                        }
                        Spacer(Modifier.height(8.dp))
                        MioButton(text = "Disabled", enabled = false, onClick = {})
                    }

                    // --- 3. 输入框 (Input) ---
                    MioCard {
                        MioText("3. 输入框 (Sizes: S/M/L)", style = MioTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        MioInput(value = "", onValueChange = {}, placeholder = "Small", size = MioSize.Small)
                        Spacer(Modifier.height(8.dp))
                        MioInput(
                            value = "",
                            onValueChange = {},
                            placeholder = "Medium (Default)",
                            size = MioSize.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        MioInput(value = "", onValueChange = {}, placeholder = "Large", size = MioSize.Large)
                    }

                    // --- 4. 标签与头像 (Tag & Avatar) ---
                    MioCard {
                        MioText("4. 标签与头像", style = MioTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MioAvatar("A", size = 48.dp)
                            Spacer(Modifier.width(16.dp))
                            MioAvatar("B", size = 32.dp, backgroundColor = MioTheme.colors.outline)
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

                    // --- 5. 选择控件 (Selection) ---
                    MioCard {
                        MioText("5. 选择控件", style = MioTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))

                        MioListItem(
                            headline = "Switch",
                            trailingContent = { MioSwitch(checked = isSwitchOn, onCheckedChange = { isSwitchOn = it }) }
                        )
                        HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.1f))

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
                    }

                    // --- 6. 状态指示 (Progress) ---
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
                        MioLinearProgress(modifier = Modifier.fillMaxWidth()) // Indeterminate
                    }

                    // --- 7. 列表与交互 (ListItem & Dialog) ---
                    MioCard(onClick = { clickCount++ }) {
                        MioListItem(
                            headline = "7. 可点击卡片 & 列表",
                            supportText = "点击此卡片测试水波纹: $clickCount",
                            leadingContent = { Icon(Icons.Default.Home, null, tint = MioTheme.colors.primary) },
                            trailingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    null,
                                    tint = MioTheme.colors.outline
                                )
                            }
                        )
                    }

                    MioButton(
                        text = "8. 弹出对话框 (Dialog)",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showDialog = true }
                    )
                }

                // --- 弹窗逻辑 ---
                if (showDialog) {
                    MioDialog(
                        onDismissRequest = { showDialog = false },
                        title = "确认操作",
                        text = "您正在调用 MioDialog 组件。该组件已适配当前的 Typography 和 Shape 设置。",
                        onConfirm = { showDialog = false }
                    )
                }
            }
        }
    }
}