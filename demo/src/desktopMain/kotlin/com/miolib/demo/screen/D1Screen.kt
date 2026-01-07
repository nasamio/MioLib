package com.miolib.demo.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miolib.demo.BuildConfig
import com.miolib.demo.api.CfD1Client
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun D1Screen(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    val d1Client = remember {
        CfD1Client(
            accountId = BuildConfig.CF_ACCOUNT_ID,
            databaseId = BuildConfig.CF_D1_DATABASE_ID,
            apiToken = BuildConfig.CF_API_TOKEN
        )
    }

    var userList by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    // 刷新数据逻辑
    val refreshData = {
        scope.launch {
            isLoading = true
            try {
                val response = d1Client.query("SELECT * FROM users")
                if (response.success && !response.result.isNullOrEmpty()) {
                    // D1 返回的结果是一个数组，取第一个结果集的 results
                    val rawRows = response.result[0].results
                    userList = rawRows?.map { element ->
                        // 使用 .jsonObject 将 JsonElement 转换为 JsonObject
                        // 使用 .jsonPrimitive.content 获取不带引号的字符串内容
                        element.jsonObject.mapValues { it.value.jsonPrimitive.content }
                    } ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                snackbarHostState.showSnackbar("读取失败: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MioText("Cloudflare D1 数据库测试", style = MioTheme.typography.titleLarge)

        // 插入数据卡片
        MioCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MioText("新增用户", style = MioTheme.typography.titleMedium)
                MioInput(value = nameInput, onValueChange = { nameInput = it }, label = "姓名")
                MioInput(value = emailInput, onValueChange = { emailInput = it }, label = "邮箱")

                MioButton(
                    text = if (isLoading) "执行中..." else "执行 INSERT SQL",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && nameInput.isNotBlank(),
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val res = d1Client.query(
                                "INSERT INTO users (name, email) VALUES (?, ?)",
                                listOf(JsonPrimitive(nameInput), JsonPrimitive(emailInput))
                            )
                            if (res.success) {
                                snackbarHostState.showSnackbar("数据写入成功")
                                nameInput = ""
                                emailInput = ""
                                refreshData()
                            } else {
                                // 打印具体的错误信息到日志
                                println("D1 写入错误: ${res.errors}")
                                snackbarHostState.showSnackbar("写入失败，请检查控制台日志")
                            }
                            isLoading = false
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MioText("用户列表", style = MioTheme.typography.titleMedium)
            IconButton(onClick = { refreshData() }, enabled = !isLoading) {
                MioIcon(Icons.Default.Refresh, tint = MioTheme.colors.primary)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                MioLoading(size = 32.dp)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(userList) { user ->
                MioCard(elevation = 1.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            MioText(user["name"] ?: "Unknown", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            MioText(user["email"] ?: "", style = MioTheme.typography.caption, color = MioTheme.colors.outline)
                        }
                        MioTag(text = "#${user["id"]}")
                    }
                }
            }
        }
    }
}