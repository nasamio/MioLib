import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings // 只是示例图标
import androidx.compose.material.icons.filled.Star // 只是示例图标
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.miolib.ui.components.* // 导入所有组件
import com.miolib.ui.theme.MioTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "MioLib 动态预览") {

        // --- 1. 定义全局状态 ---
        // 这个变量控制整个 Demo 是深色还是浅色
        var isDarkTheme by remember { mutableStateOf(false) }

        // 演示用的组件状态
        var textValue by remember { mutableStateOf("") }
        var isSwitchOn by remember { mutableStateOf(false) }

        // --- 2. 顶层包裹 Theme，并将状态传入 ---
        MioTheme(darkTheme = isDarkTheme) {

            // --- 3. 使用 MioScaffold 构建页面结构 ---
            MioScaffold(
                topBar = {
                    MioTopBar(
                        title = if (isDarkTheme) "深色模式" else "浅色模式",
                        // 左侧返回按钮模拟
                        onBackClick = { println("点击了返回") },
                        // 右侧放置一个主题切换开关
                        actions = {
                            // 这里放一个简单的 Switch 来控制主题
                            MioSwitch(
                                checked = isDarkTheme,
                                onCheckedChange = { isDarkTheme = it }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    )
                }
            ) { padding -> // 获取 padding，防止内容被 TopBar 遮挡

                // --- 4. 页面内容区域 ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding) // 关键：应用 Scaffold 给的 padding
                        .padding(24.dp)   // 额外的内部间距
                ) {

                    // 区域 1：展示表单
                    MioCard {
                        MioText("基础组件测试", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        MioInput(
                            value = textValue,
                            onValueChange = { textValue = it },
                            label = "测试输入框",
                            placeholder = "输入点什么看看..."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MioText("去除阴影后的开关：")
                            Spacer(modifier = Modifier.width(8.dp))
                            // 测试新的开关
                            MioSwitch(checked = isSwitchOn, onCheckedChange = { isSwitchOn = it })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 区域 2：展示标签和进度
                    MioCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MioText("状态展示")
                            // 动态改变标签颜色
                            MioTag(text = if(isSwitchOn) "已开启" else "已关闭")
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        MioText("加载中...", fontSize = 12.sp, color = MioTheme.colors.outline)
                        Spacer(modifier = Modifier.height(8.dp))
                        MioLinearProgress(progress = 0.45f)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 区域 3：按钮测试
                    MioButton(
                        text = "切换主题 (也可以点这里)",
                        onClick = { isDarkTheme = !isDarkTheme }, // 按钮也能控制主题
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}