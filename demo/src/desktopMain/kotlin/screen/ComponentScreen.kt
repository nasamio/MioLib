@file:OptIn(ExperimentalMaterial3Api::class)

package screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioSize
import com.miolib.ui.theme.MioTheme
import kotlinx.coroutines.launch

// ==========================================
// Screen 1: 组件展示
// ==========================================
@Composable
fun ComponentScreen(
    snackbarHostState: SnackbarHostState,
    useAndroidSize: Boolean,
    onUseAndroidSizeChange: (Boolean) -> Unit,
    useSquareShape: Boolean,
    onUseSquareShapeChange: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // 局部状态
    var inputText by remember { mutableStateOf("") }
    var isSwitchOn by remember { mutableStateOf(true) }
    var isChecked by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(0.5f) }
    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- Part 0: 样式控制 ---
        MioCard {
            MioText("0. 样式控制台", style = MioTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                MioSwitch(checked = useAndroidSize, onCheckedChange = onUseAndroidSizeChange)
                Spacer(Modifier.width(8.dp))
                MioText("移动端尺寸", style = MioTheme.typography.body)
                Spacer(Modifier.width(24.dp))
                MioSwitch(checked = useSquareShape, onCheckedChange = onUseSquareShapeChange)
                Spacer(Modifier.width(8.dp))
                MioText("直角风格", style = MioTheme.typography.body)
            }
        }

        // --- Part 1: 排版 ---
        MioCard {
            MioText("1. 排版系统 (Typography)", style = MioTheme.typography.titleMedium)
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MioTheme.colors.outline.copy(alpha = 0.2f))
            MioText("Display Title", style = MioTheme.typography.display)
            MioText("Title Large", style = MioTheme.typography.titleLarge)
            MioText("Body Text: 正文内容，清晰易读。", style = MioTheme.typography.body)
            MioText("Caption Text: 辅助说明文字", style = MioTheme.typography.caption, color = MioTheme.colors.outline)
        }

        // --- Part 2: 按钮 ---
        MioCard {
            MioText("2. 按钮与反馈", style = MioTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                MioButton("Small", size = MioSize.Small, onClick = {})
                Spacer(Modifier.width(8.dp))
                MioButton("Medium", size = MioSize.Medium, onClick = {})
                Spacer(Modifier.width(8.dp))
                MioButton("Toast", backgroundColor = MioTheme.colors.outline, onClick = {
                    scope.launch { snackbarHostState.showSnackbar("Mio 风格消息提示！") }
                })
            }
        }

        // --- Part 3: 输入框 ---
        MioCard {
            MioText("3. 输入框", style = MioTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            MioInput(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = "请输入内容...",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Part 5: 选择与滑动 ---
        MioCard {
            MioText("5. 交互组件", style = MioTheme.typography.titleMedium)
            MioListItem(
                headline = "Switch",
                trailingContent = { MioSwitch(checked = isSwitchOn, onCheckedChange = { isSwitchOn = it }) })
            HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.1f))
            MioListItem(
                headline = "CheckBox",
                trailingContent = { MioCheckBox(checked = isChecked, onCheckedChange = { isChecked = it }) },
                onClick = { isChecked = !isChecked })
            HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.1f))
            Column(Modifier.padding(top = 8.dp)) {
                MioText("Slider: ${(sliderValue * 100).toInt()}%", style = MioTheme.typography.caption)
                MioSlider(value = sliderValue, onValueChange = { sliderValue = it })
            }
        }

        // --- Part 7: 弹窗 ---
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MioButton("打开 Dialog", modifier = Modifier.weight(1f), onClick = { showDialog = true })
            MioButton(
                "打开 BottomSheet",
                modifier = Modifier.weight(1f),
                backgroundColor = MioTheme.colors.surface,
                contentColor = MioTheme.colors.primary,
                onClick = { showBottomSheet = true })
        }

        // --- Part 10: 图片 ---
        MioCard {
            MioText("10. 图片查看器", style = MioTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Row {
                MioImage(imageVector = Icons.Default.Face, size = 80.dp, contentDescription = "Face")
                Spacer(Modifier.width(16.dp))
                MioImage(imageVector = Icons.Default.LocationOn, size = 80.dp, cornerRadius = 40.dp)
            }
        }
    }

    // 弹窗处理
    if (showDialog) {
        MioDialog(
            onDismissRequest = { showDialog = false },
            title = "Mio Dialog",
            text = "这是一个标准的对话框组件。",
            onConfirm = { showDialog = false }
        )
    }
    if (showBottomSheet) {
        MioBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MioText("Mio BottomSheet", style = MioTheme.typography.titleLarge)
                Spacer(Modifier.height(32.dp))
                MioButton("关闭", modifier = Modifier.fillMaxWidth(), onClick = { showBottomSheet = false })
            }
        }
    }
}