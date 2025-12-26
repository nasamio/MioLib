package screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.miolib.smms.SmmsClient
import com.miolib.smms.SmmsData
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

// --- 图片内存缓存 ---
object SmmsImageCache {
    val bitmapCache = mutableMapOf<String, ImageBitmap>()
}

// ==========================================
// SMMS 图床主界面
// ==========================================
@Composable
fun SmmsScreen(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()

    // 状态管理
    var smmsToken by remember { mutableStateOf("Cac8ge6lwmQ7CCLENFZ3KAFLYJ6s6AbI") }
    var historyList by remember { mutableStateOf<List<SmmsData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // 大图查看状态
    var viewingUrl by remember { mutableStateOf<String?>(null) }

    // 用户信息
    var userName by remember { mutableStateOf("未登录") }
    var diskUsage by remember { mutableStateOf("---") }
    var diskLimit by remember { mutableStateOf("---") }
    var userRole by remember { mutableStateOf("") }

    // 辅助：排序
    fun sortList(list: List<SmmsData>): List<SmmsData> {
        return list.sortedWith(compareByDescending<SmmsData> {
            if (it.hash?.startsWith("temp_") == true) Long.MAX_VALUE else 0L
        }.thenByDescending {
            it.createdAt?.toLongOrNull() ?: 0L
        })
    }

    fun copyToClipboard(text: String) {
        try {
            val selection = StringSelection(text)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 动作：刷新数据
    suspend fun refreshData() {
        if (smmsToken.isBlank()) return
        isLoading = true
        val client = SmmsClient(smmsToken)
        val profileJob = scope.launch {
            val res = client.getProfile()
            val data = res.data
            if (res.success && data != null) {
                userName = data.username
                userRole = data.role
                diskUsage = data.diskUsage
                diskLimit = data.diskLimit
            }
        }
        val historyJob = scope.launch {
            val res = client.getHistory()
            if (res.success) {
                val currentTemps = historyList.filter { it.hash?.startsWith("temp_") == true }
                val newItems = res.data ?: emptyList()
                historyList = sortList(currentTemps + newItems)
            }
        }
        joinAll(profileJob, historyJob)
        isLoading = false
    }

    // 动作：上传文件 (乐观UI)
    fun uploadFile(file: File) {
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { file.readBytes() }
                val localBitmap = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
                val tempKey = "local_preview_${file.name}_${System.currentTimeMillis()}"

                SmmsImageCache.bitmapCache[tempKey] = localBitmap

                val tempItem = SmmsData(
                    filename = file.name,
                    width = 0, height = 0, size = bytes.size,
                    url = tempKey,
                    hash = "temp_${System.currentTimeMillis()}",
                    createdAt = "0"
                )

                historyList = listOf(tempItem) + historyList

                val client = SmmsClient(smmsToken)
                val res = client.uploadImage(file.name, bytes)

                if (res.success && res.data != null) {
                    val serverItem = res.data
                    historyList = historyList.map { (if (it.hash == tempItem.hash) serverItem else it) as SmmsData }
                    snackbarHostState.showSnackbar("上传成功: ${file.name}")
                    // 刷新容量
                    launch {
                        val profile = client.getProfile()
                        profile.data?.let { diskUsage = it.diskUsage }
                    }
                } else {
                    historyList = historyList.filter { it.hash != tempItem.hash }
                    snackbarHostState.showSnackbar("上传失败: ${res.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                snackbarHostState.showSnackbar("错误: ${e.message}")
                historyList = historyList.filter { it.filename != file.name }
            }
        }
    }

    // 动作：删除
    fun deleteItem(item: SmmsData) {
        historyList = historyList.filter { it.hash != item.hash }
        scope.launch {
            val client = SmmsClient(smmsToken)
            val res = client.deleteImage(item.hash ?: "")
            if (!res.success) {
                snackbarHostState.showSnackbar("服务器删除失败: ${res.message}")
            } else {
                launch {
                    val profile = client.getProfile()
                    profile.data?.let { diskUsage = it.diskUsage }
                }
            }
        }
    }

    fun openFilePicker() {
        scope.launch(Dispatchers.IO) {
            val chooser = JFileChooser()
            chooser.dialogTitle = "选择图片上传"
            chooser.fileFilter = FileNameExtensionFilter("图片", "jpg", "jpeg", "png", "gif", "bmp")
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                uploadFile(file)
            }
        }
    }

    LaunchedEffect(Unit) {
        scope.launch { refreshData() }
    }

    // --- 界面布局 ---
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard
        MioCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MioAvatar(text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "?", size = 48.dp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MioText(userName, style = MioTheme.typography.titleMedium)
                            if (userRole.isNotEmpty()) {
                                Spacer(Modifier.width(8.dp))
                                MioTag(userRole)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        MioText(
                            "已用: $diskUsage / 总计: $diskLimit",
                            style = MioTheme.typography.caption,
                            // [修改]：不再直接用 outline，而是用主文字颜色降低不透明度，这样在深色模式下更亮、更清楚
                            color = MioTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { scope.launch { refreshData() } }) {
                        Icon(Icons.Default.Refresh, "Refresh", tint = MioTheme.colors.primary)
                    }
                    if (isLoading) {
                        MioLoading(size = 24.dp)
                        Spacer(Modifier.width(16.dp))
                    }
                    MioButton("上传图片", onClick = { openFilePicker() })
                }
            }
        }

        // Image Grid
        MioText("我的相册 (${historyList.size})", style = MioTheme.typography.titleMedium)

        if (historyList.isEmpty() && !isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MioText("暂无图片", color = MioTheme.colors.outline)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 180.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items = historyList, key = { it.hash ?: it.url ?: it.hashCode() }) { item ->
                    ImageGridItem(
                        item = item,
                        smmsToken = smmsToken,
                        onDelete = { deleteItem(item) },
                        onView = { viewingUrl = item.url }, // 左键：查看大图
                        onCopy = { // 右键：复制
                            item.url?.let {
                                copyToClipboard(it)
                                scope.launch { snackbarHostState.showSnackbar("已复制: $it") }
                            }
                        }
                    )
                }
            }
        }
    }

    // 全屏查看器 Popup
    if (viewingUrl != null) {
        FullScreenImageViewer(
            url = viewingUrl!!,
            token = smmsToken,
            onDismiss = { viewingUrl = null }
        )
    }
}

// --- 卡片组件 ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGridItem(
    item: SmmsData,
    smmsToken: String,
    onDelete: () -> Unit,
    onView: () -> Unit,
    onCopy: () -> Unit,
) {
    val isUploading = item.hash?.startsWith("temp_") == true

    MioCard(
        modifier = Modifier.fillMaxWidth().height(240.dp),
        elevation = 2.dp
        // 这里的 onClick 留空，使用内部 Box 处理具体的左右键逻辑
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. 上半部分：图片区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MioTheme.colors.surface)
                    // 修复：使用 PointerMatcher.Primary 和 PointerMatcher.mouse(PointerButton.Secondary)
                    .onClick(
                        matcher = PointerMatcher.Primary,
                        onClick = if (isUploading) {
                            {}
                        } else onView
                    )
                    .onClick(
                        matcher = PointerMatcher.mouse(PointerButton.Secondary),
                        onClick = if (isUploading) {
                            {}
                        } else onCopy
                    )
            ) {
                if (!item.url.isNullOrBlank()) {
                    AsyncNetworkImage(
                        url = item.url!!,
                        token = smmsToken,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Create, null, tint = Color.Gray)
                    }
                }

                if (isUploading) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                // 优化后的删除按钮：圆形半透明背景
                Surface(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.4f), // 半透明黑底
                    contentColor = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 提示条
                if (!isUploading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MioText(
                            text = "左键大图 • 右键复制",
                            style = MioTheme.typography.caption,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.dp.value.toTextUnit() // 稍微小一点
                        )
                    }
                }
            }

            // 2. 下半部分：文字信息
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                MioText(
                    text = item.filename ?: "Unknown",
                    style = MioTheme.typography.body,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                if (isUploading) {
                    MioText("正在上传...", style = MioTheme.typography.caption, color = MioTheme.colors.primary)
                } else {
                    MioText(
                        text = "${item.width} x ${item.height} • ${(item.size?.div(1024))} KB",
                        style = MioTheme.typography.caption,
                        // [修改]：同样使用 onSurface + alpha，保证文字清晰可见
                        color = MioTheme.colors.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// --- 简单的扩展函数用于 dp 转 sp (仅限此处使用) ---
@Composable
private fun Float.toTextUnit() = this.sp

// --- 网络图片加载 ---
@Composable
fun AsyncNetworkImage(
    url: String,
    token: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val cachedBitmap = SmmsImageCache.bitmapCache[url]
    var imageBitmap by remember(url) { mutableStateOf(cachedBitmap) }
    var isError by remember { mutableStateOf(false) }

    if (imageBitmap == null && !isError) {
        LaunchedEffect(url) {
            try {
                if (SmmsImageCache.bitmapCache.containsKey(url)) {
                    imageBitmap = SmmsImageCache.bitmapCache[url]
                    return@LaunchedEffect
                }
                if (url.startsWith("local_preview")) return@LaunchedEffect

                val bytes = withContext(Dispatchers.IO) {
                    val client = SmmsClient(token)
                    client.downloadImage(url)
                }
                if (bytes != null && bytes.isNotEmpty()) {
                    val bitmap = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
                    SmmsImageCache.bitmapCache[url] = bitmap
                    imageBitmap = bitmap
                } else {
                    isError = true
                }
            } catch (e: CancellationException) {
                // 关键修改：重新抛出 CancellationException，让 Compose 正常处理取消逻辑
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                isError = true
            }
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    } else if (isError) {
        Box(modifier, contentAlignment = Alignment.Center) {
            MioText("!", style = MioTheme.typography.caption, color = Color.Red)
        }
    } else {
        Box(modifier, contentAlignment = Alignment.Center) {
            MioLoading(size = 20.dp)
        }
    }
}

// --- 全屏大图查看器 ---
@Composable
fun FullScreenImageViewer(
    url: String,
    token: String,
    onDismiss: () -> Unit,
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val animatedScale by animateFloatAsState(targetValue = scale)

    Popup(alignment = Alignment.Center, onDismissRequest = onDismiss) {
        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                val maxTx = (1000f * (scale - 1)) / 2
                                val maxTy = (1000f * (scale - 1)) / 2
                                offsetX = (offsetX + pan.x * scale).coerceIn(-maxTx, maxTx)
                                offsetY = (offsetY + pan.y * scale).coerceIn(-maxTy, maxTy)
                            } else {
                                offsetX = 0f; offsetY = 0f
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismiss() },
                            onDoubleTap = {
                                if (scale > 1f) {
                                    scale = 1f; offsetX = 0f; offsetY = 0f
                                } else {
                                    scale = 2.5f
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncNetworkImage(
                    url = url,
                    token = token,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = animatedScale,
                            scaleY = animatedScale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit
                )

                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)) {
                    MioText("双击缩放 • 单击关闭", color = Color.White.copy(0.6f), style = MioTheme.typography.caption)
                }
            }
        }
    }
}