package com.miolib.demo.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miolib.demo.BuildConfig
import com.miolib.demo.api.CfKvClient
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import kotlinx.coroutines.launch

@Composable
fun CfKvScreen(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()

    // 初始化客户端
    val kvClient = remember {
        CfKvClient(
            accountId = BuildConfig.CF_ACCOUNT_ID,
            namespaceId = BuildConfig.CF_KV_NAMESPACE_ID,
            apiToken = BuildConfig.CF_API_TOKEN
        )
    }

    var keys by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var keyInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }

    // 刷新列表函数
    val refreshKeys = {
        scope.launch {
            isLoading = true
            keys = kvClient.listKeys()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refreshKeys() }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MioText("Cloudflare KV 存储管理", style = MioTheme.typography.titleLarge)

        // 写入操作卡片
        MioCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MioText("写入键值对", style = MioTheme.typography.titleMedium)
                MioInput(value = keyInput, onValueChange = { keyInput = it }, label = "Key (键)")
                MioInput(value = valueInput, onValueChange = { valueInput = it }, label = "Value (值)")

                MioButton(
                    text = "提交至远程 KV",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = keyInput.isNotBlank() && !isLoading,
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val success = kvClient.putValue(keyInput, valueInput)
                            if (success) {
                                snackbarHostState.showSnackbar("写入成功")
                                keyInput = ""; valueInput = ""
                                keys = kvClient.listKeys()
                            } else {
                                snackbarHostState.showSnackbar("写入失败，请检查配置")
                            }
                            isLoading = false
                        }
                    }
                )
            }
        }

        // 列表表头
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MioText("远程 Keys (${keys.size})", style = MioTheme.typography.titleMedium)
            IconButton(onClick = { refreshKeys() }, enabled = !isLoading) {
                MioIcon(Icons.Default.Refresh, tint = MioTheme.colors.primary)
            }
        }

        if (isLoading) MioLoading(modifier = Modifier.align(Alignment.CenterHorizontally))

        // Key 列表
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(keys) { key ->
                var remoteValue by remember { mutableStateOf<String?>(null) }

                MioCard(elevation = 1.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            MioText(
                                key,
                                color = MioTheme.colors.primary,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            if (remoteValue != null) {
                                MioText("Value: $remoteValue", style = MioTheme.typography.caption)
                            }
                        }

                        // 查询 Value 按钮
                        TextButton(onClick = {
                            scope.launch {
                                val v = kvClient.getValue(key)
                                remoteValue = v ?: "读取失败"
                            }
                        }) {
                            MioText("读取值", style = MioTheme.typography.label)
                        }

                        // 删除按钮
                        IconButton(onClick = {
                            scope.launch {
                                if (kvClient.deleteKey(key)) {
                                    keys = kvClient.listKeys()
                                    snackbarHostState.showSnackbar("已删除: $key")
                                }
                            }
                        }) {
                            MioIcon(Icons.Default.Delete, tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}