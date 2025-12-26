package screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miolib.smms.SmmsClient
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import kotlinx.coroutines.launch

// ==========================================
// SMMS测试
// ==========================================
@Composable
fun SmmsScreen(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    var smmsToken by remember { mutableStateOf("Cac8ge6lwmQ7CCLENFZ3KAFLYJ6s6AbI") }
    var uploadStatus by remember { mutableStateOf("等待上传...") }
    var uploadedImageUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        MioCard {
            MioText("SM.MS 图床上传测试", style = MioTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            MioText(
                "本页面演示了使用 Ktor 调用 SM.MS API 进行图片上传。",
                style = MioTheme.typography.caption,
                color = MioTheme.colors.outline
            )
            Spacer(Modifier.height(24.dp))

            MioInput(
                value = smmsToken,
                onValueChange = { smmsToken = it },
                label = "API Token (Required)",
                placeholder = "请输入 SM.MS Secret Token",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            MioButton(
                text = "模拟上传测试图片",
                onClick = {
                    if (smmsToken.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("请先输入 Token") }
                        return@MioButton
                    }

                    uploadStatus = "正在上传..."
                    scope.launch {
                        // 1. 构造一个测试用的 1x1 GIF 字节数组
                        val dummyImage = byteArrayOf(
                            0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
                            0x80.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x21, 0xF9.toByte(), 0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x2C,
                            0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02,
                            0x02, 0x44, 0x01, 0x00, 0x3B
                        )

                        // 2. 调用 smms 模块的 Client
                        val client = SmmsClient(smmsToken)
                        val result = client.uploadImage("test_pixel.gif", dummyImage)

                        if (result.success) {
                            uploadStatus = "上传成功！"
                            uploadedImageUrl = result.data?.url ?: ""
                            snackbarHostState.showSnackbar("上传成功: ${result.data?.url}")
                        } else {
                            uploadStatus = "失败: ${result.message}"
                            snackbarHostState.showSnackbar("上传失败: ${result.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.1f))
            Spacer(Modifier.height(16.dp))

            MioListItem(
                headline = "当前状态",
                trailingContent = {
                    MioText(
                        uploadStatus,
                        color = if (uploadStatus.contains("失败")) Color.Red else MioTheme.colors.primary
                    )
                }
            )

            if (uploadedImageUrl.isNotEmpty()) {
                MioCard(
                    backgroundColor = MioTheme.colors.background,
                    hasBorder = true,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Column {
                        MioText("图片 URL:", style = MioTheme.typography.label)
                        Spacer(Modifier.height(4.dp))
                        MioText(uploadedImageUrl, style = MioTheme.typography.body, color = MioTheme.colors.primary)
                    }
                }
            }
        }
    }
}