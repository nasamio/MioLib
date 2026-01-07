package com.miolib.demo.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager // 获取系统剪切板
import androidx.compose.ui.text.AnnotatedString // 用于剪切板文本包装
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import com.miolib.demo.BuildConfig
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.fromFile
import aws.smithy.kotlin.runtime.net.url.Url
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser

@Composable
fun R2Screen(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current // 初始化剪切板管理器

    // 检查配置是否缺失
    val isConfigMissing = BuildConfig.R2_ACCESS_KEY.isBlank() ||
            BuildConfig.R2_SECRET_KEY.isBlank() ||
            BuildConfig.R2_BUCKET_NAME.isBlank()

    // 使用 BuildConfig 中的值初始化 UI 状态
    var endpoint by remember { mutableStateOf(BuildConfig.R2_ENDPOINT) }
    var accessKey by remember { mutableStateOf(BuildConfig.R2_ACCESS_KEY) }
    var secretKey by remember { mutableStateOf(BuildConfig.R2_SECRET_KEY) }
    var bucketName by remember { mutableStateOf(BuildConfig.R2_BUCKET_NAME) }

    var isUploading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MioText("Cloudflare R2 存储管理", style = MioTheme.typography.titleLarge)

        if (isConfigMissing) {
            // 如果配置缺失，显示警告卡片
            MioCard(
                backgroundColor = MioTheme.colors.primary.copy(alpha = 0.1f),
                hasBorder = true
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MioText("⚠️ 配置缺失", style = MioTheme.typography.titleMedium, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    MioText(
                        "检测到 R2 API 配置为空。请在项目根目录的 local.properties 中填写参数并重新运行。点击下方模板复制：",
                        style = MioTheme.typography.body
                    )

                    // 定义需要复制的原始字符串
                    val copyContent = """
                        R2_ENDPOINT=https://<your-account-id>.r2.cloudflarestorage.com
                        R2_ACCESS_KEY=<your-access-key>
                        R2_SECRET_KEY=<your-secret-key>
                        R2_BUCKET_NAME=<your-bucket-name>
                    """.trimIndent()

                    // 可复制的文本
                    ClickableText(
                        text = buildAnnotatedString {
                            append(copyContent)
                        },
                        style = MioTheme.typography.body.copy(color = MioTheme.colors.primary), // 使用主题色提示可点击
                        onClick = {
                            // 1. 执行复制到剪切板
                            clipboardManager.setText(AnnotatedString(copyContent))
                            // 2. 弹出提示确认
                            scope.launch {
                                snackbarHostState.showSnackbar("配置模板已复制到剪切板")
                            }
                        }
                    )
                }
            }
        }

        // 配置信息展示
        MioCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MioText("当前 API 配置", style = MioTheme.typography.titleMedium)
                MioInput(value = endpoint, onValueChange = { endpoint = it }, label = "Endpoint URL")
                MioInput(value = accessKey, onValueChange = { accessKey = it }, label = "Access Key")
                MioInput(value = secretKey, onValueChange = { secretKey = it }, label = "Secret Key (Hidden)")
                MioInput(value = bucketName, onValueChange = { bucketName = it }, label = "Bucket Name")
            }
        }

        // 上传按钮
        MioCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    MioText("文件上传", style = MioTheme.typography.titleMedium)
                    MioText("支持将本地文件上传至 Cloudflare R2", style = MioTheme.typography.caption)
                }

                MioButton(
                    text = if (isUploading) "上传中..." else "选择文件",
                    enabled = !isUploading && accessKey.isNotBlank(),
                    onClick = {
                        scope.launch {
                            val chooser = JFileChooser()
                            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                val file = chooser.selectedFile
                                isUploading = true
                                val success = uploadToR2(
                                    file, endpoint, accessKey, secretKey, bucketName
                                )
                                isUploading = false
                                snackbarHostState.showSnackbar(if (success) "文件 ${file.name} 上传成功！" else "上传失败，请检查配置")
                            }
                        }
                    }
                )
            }
        }
    }
}

private suspend fun uploadToR2(
    file: File,
    endpoint: String,
    accessKey: String,
    secretKey: String,
    bucket: String
): Boolean {
    return try {
        val s3Client = S3Client {
            region = "auto"
            endpointUrl = Url.parse(endpoint)
            credentialsProvider = StaticCredentialsProvider {
                accessKeyId = accessKey
                secretAccessKey = secretKey
            }
        }

        s3Client.putObject(PutObjectRequest {
            this.bucket = bucket
            this.key = file.name
            this.body = ByteStream.fromFile(file)
        })
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
