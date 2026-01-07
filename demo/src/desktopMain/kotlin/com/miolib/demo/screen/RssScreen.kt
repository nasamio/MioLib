package com.miolib.demo.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miolib.rss.RssClient
import com.miolib.rss.RssDefaults
import com.miolib.rss.RssItem
import com.miolib.rss.RssSource
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI

@Composable
fun RssScreen(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    val client = remember { RssClient() }

    // --- 状态管理 ---
    // 1. 所有订阅源 (初始化使用 RssDefaults 中的国内源)
    var feeds by remember { mutableStateOf(RssDefaults.defaultFeeds) }

    // 2. 当前选中的 Tab 索引
    var selectedTabIndex by remember { mutableStateOf(0) }

    // 3. 当前 Tab 的文章列表
    var rssItems by remember { mutableStateOf<List<RssItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // 4. 当前查看的文章 (如果不为 null 则显示阅读器)
    var readingItem by remember { mutableStateOf<RssItem?>(null) }

    // 5. 添加订阅的弹窗状态
    var showAddDialog by remember { mutableStateOf(false) }

    // --- 辅助函数 ---
    fun loadFeed(index: Int) {
        if (index !in feeds.indices) return
        val url = feeds[index].url
        scope.launch {
            isLoading = true
            rssItems = emptyList() // 切换时先清空，避免显示旧数据
            rssItems = client.getRssFeed(url)
            isLoading = false
            if (rssItems.isEmpty()) {
                snackbarHostState.showSnackbar("暂无内容或加载失败，请检查网络")
            }
        }
    }

    // 监听 Tab 切换
    LaunchedEffect(selectedTabIndex) {
        loadFeed(selectedTabIndex)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- 顶部栏 (包含 Tab 和 添加按钮) ---
            MioCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                backgroundColor = MioTheme.colors.surface,
                hasBorder = false
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 栏 (占据大部分空间)
                    Box(modifier = Modifier.weight(1f)) {
                        MioTabBar(
                            items = feeds.map { MioTabItem(it.name) },
                            selectedIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it },
                            type = MioTabType.Top
                        )
                    }

                    // 添加按钮
                    IconButton(onClick = { showAddDialog = true }) {
                        MioIcon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Feed",
                            tint = MioTheme.colors.primary
                        )
                    }
                }
            }

            // --- 内容列表 ---
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    MioLoading(size = 48.dp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (rssItems.isEmpty() && !isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                MioText("这里空空如也...", color = MioTheme.colors.outline)
                            }
                        }
                    }

                    items(rssItems) { item ->
                        RssItemCard(item) {
                            // 点击进入阅读模式
                            readingItem = item
                        }
                    }
                }
            }
        }

        // --- 阅读器视图 (覆盖层) ---
        AnimatedVisibility(
            visible = readingItem != null,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            readingItem?.let { item ->
                RssReaderView(
                    item = item,
                    onBack = { readingItem = null }
                )
            }
        }

        // --- 添加订阅弹窗 ---
        if (showAddDialog) {
            AddFeedDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, url ->
                    if (name.isNotBlank() && url.isNotBlank()) {
                        feeds = feeds + RssSource(name, url)
                        // 自动切换到新添加的 Tab
                        selectedTabIndex = feeds.lastIndex
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

// --- 组件：列表卡片 ---
@Composable
fun RssItemCard(item: RssItem, onClick: () -> Unit) {
    MioCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = 1.dp
    ) {
        Column {
            MioText(
                text = item.title,
                style = MioTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            MioText(
                text = item.pubDate,
                style = MioTheme.typography.caption,
                color = MioTheme.colors.outline
            )
            Spacer(Modifier.height(8.dp))
            MioText(
                text = item.description,
                style = MioTheme.typography.body,
                maxLines = 3,
                color = MioTheme.colors.onSurface.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(12.dp))
            MioText(
                text = "点击查看详情",
                style = MioTheme.typography.label,
                color = MioTheme.colors.primary
            )
        }
    }
}

@Composable
fun RssReaderView(item: RssItem, onBack: () -> Unit) {
    MioScaffold(
        topBar = {
            MioTopBar(
                title = "阅读模式",
                onBackClick = onBack,
                backgroundColor = MioTheme.colors.surface
            )
        },
        containerColor = MioTheme.colors.background
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // 1. 标题区域
            SelectionContainer {
                Column {
                    MioText(
                        text = item.title,
                        style = MioTheme.typography.display,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MioTag(text = "RSS", backgroundColor = MioTheme.colors.primary.copy(alpha = 0.1f))
                        Spacer(Modifier.width(12.dp))
                        MioText(text = item.pubDate, style = MioTheme.typography.caption, color = MioTheme.colors.outline)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MioTheme.colors.outline.copy(alpha = 0.2f))
            Spacer(Modifier.height(24.dp))

            // 2. 正文区域
            SelectionContainer {
                MioText(
                    text = item.content.ifBlank { "内容解析为空" },
                    style = MioTheme.typography.body,
                    fontSize = 17.sp,
                    lineHeight = 30.sp,
                    color = MioTheme.colors.onSurface.copy(alpha = 0.9f)
                )
            }

            Spacer(Modifier.height(48.dp))

            // 3. 底部状态与操作
            // 如果内容很短（通常意味着是摘要），显示醒目的按钮
            val isShortContent = item.content.length < 500

            if (isShortContent) {
                MioCard(
                    backgroundColor = MioTheme.colors.surface,
                    hasBorder = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MioText(
                            text = "当前源仅提供摘要，无法在 App 内加载全文。",
                            style = MioTheme.typography.caption,
                            color = MioTheme.colors.outline
                        )
                        Spacer(Modifier.height(12.dp))
                        MioButton(
                            text = "在浏览器中打开原文",
                            onClick = {
                                try {
                                    Desktop.getDesktop().browse(URI(item.link))
                                } catch (e: Exception) { e.printStackTrace() }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    MioText("End of Article", style = MioTheme.typography.caption, color = MioTheme.colors.outline)
                }
            }
        }
    }
}

// --- 组件：添加订阅弹窗 ---
@Composable
fun AddFeedDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    MioDialog(
        visible = true,
        onDismissRequest = onDismiss
    ) {
        MioCard(
            modifier = Modifier.width(400.dp),
            onClick = null // 禁用 Card 点击
        ) {
            Column {
                MioText("添加订阅", style = MioTheme.typography.titleLarge)
                Spacer(Modifier.height(24.dp))

                MioInput(
                    value = name,
                    onValueChange = { name = it },
                    label = "名称 (如: TechCrunch)",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                MioInput(
                    value = url,
                    onValueChange = { url = it },
                    label = "RSS 地址",
                    placeholder = "https://...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        MioText("取消", color = MioTheme.colors.outline)
                    }
                    Spacer(Modifier.width(8.dp))
                    MioButton(
                        text = "添加",
                        onClick = { onConfirm(name, url) },
                        enabled = name.isNotBlank() && url.isNotBlank()
                    )
                }
            }
        }
    }
}