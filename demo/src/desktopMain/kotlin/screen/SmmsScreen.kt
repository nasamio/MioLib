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

// ... [省略 SmmsImageCache 对象，内容不变] ...
object SmmsImageCache {
    val bitmapCache = mutableMapOf<String, ImageBitmap>()
}

@Composable
fun SmmsScreen(snackbarHostState: SnackbarHostState) {
    // ... [省略状态和逻辑代码，保持不变] ...
    val scope = rememberCoroutineScope()
    var smmsToken by remember { mutableStateOf("Cac8ge6lwmQ7CCLENFZ3KAFLYJ6s6AbI") }
    var historyList by remember { mutableStateOf<List<SmmsData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var viewingUrl by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf("未登录") }
    var diskUsage by remember { mutableStateOf("---") }
    var diskLimit by remember { mutableStateOf("---") }
    var userRole by remember { mutableStateOf("") }

    // ... [省略辅助函数：sortList, copyToClipboard, refreshData, uploadFile, deleteItem, openFilePicker] ...
    // (逻辑代码不变，此处省略以节省篇幅，重点在下方的 UI 替换)

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
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun refreshData() {
        if (smmsToken.isBlank()) return
        isLoading = true
        val client = SmmsClient(smmsToken)
        val profileJob = scope.launch {
            val res = client.getProfile()
            val data = res.data
            if (res.success && data != null) {
                userName = data.username; userRole = data.role; diskUsage = data.diskUsage; diskLimit = data.diskLimit
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

    fun uploadFile(file: File) {
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { file.readBytes() }
                val localBitmap = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
                val tempKey = "local_preview_${file.name}_${System.currentTimeMillis()}"
                SmmsImageCache.bitmapCache[tempKey] = localBitmap
                val tempItem = SmmsData(filename = file.name, width = 0, height = 0, size = bytes.size, url = tempKey, hash = "temp_${System.currentTimeMillis()}", createdAt = "0")
                historyList = listOf(tempItem) + historyList
                val client = SmmsClient(smmsToken)
                val res = client.uploadImage(file.name, bytes)
                if (res.success && res.data != null) {
                    val serverItem = res.data
                    historyList = historyList.map { (if (it.hash == tempItem.hash) serverItem else it) as SmmsData }
                    snackbarHostState.showSnackbar("上传成功: ${file.name}")
                    launch { client.getProfile().data?.let { diskUsage = it.diskUsage } }
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

    fun deleteItem(item: SmmsData) {
        historyList = historyList.filter { it.hash != item.hash }
        scope.launch {
            val client = SmmsClient(smmsToken)
            val res = client.deleteImage(item.hash ?: "")
            if (!res.success) {
                snackbarHostState.showSnackbar("服务器删除失败: ${res.message}")
            } else {
                launch { client.getProfile().data?.let { diskUsage = it.diskUsage } }
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
                            color = MioTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { scope.launch { refreshData() } }) {
                        // [Update] 使用 MioIcon
                        MioIcon(Icons.Default.Refresh, "Refresh", tint = MioTheme.colors.primary)
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
                        onView = { viewingUrl = item.url },
                        onCopy = {
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
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MioTheme.colors.surface)
                    .onClick(
                        matcher = PointerMatcher.Primary,
                        onClick = if (isUploading) { {} } else onView
                    )
                    .onClick(
                        matcher = PointerMatcher.mouse(PointerButton.Secondary),
                        onClick = if (isUploading) { {} } else onCopy
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
                        // [Update] 使用 MioIcon
                        MioIcon(Icons.Default.Create, null, tint = Color.Gray)
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

                Surface(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(28.dp),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.4f),
                    contentColor = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // [Update] 使用 MioIcon
                        MioIcon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (!isUploading) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color.Black.copy(alpha = 0.6f)).padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MioText(
                            text = "左键大图 • 右键复制",
                            style = MioTheme.typography.caption,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.dp.value.toTextUnit()
                        )
                    }
                }
            }

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
                        color = MioTheme.colors.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun Float.toTextUnit() = this.sp

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
            } catch (e: CancellationException) { throw e } catch (e: Exception) { e.printStackTrace(); isError = true }
        }
    }

    if (imageBitmap != null) {
        // [Update] 使用 MioImage (标准模式)
        // 使用 Painter 版本的 MioImage
        MioImage(
            painter = androidx.compose.ui.graphics.painter.BitmapPainter(imageBitmap!!),
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
            enableClickToExpand = false // 列表页已经有点击逻辑，这里不开启内部的点击
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

@Composable
fun FullScreenImageViewer(
    url: String,
    token: String,
    onDismiss: () -> Unit,
) {
    // 复用 MioImage 的内置全屏逻辑比较困难，因为需要异步加载。
    // 这里保持原有的 FullScreenImageViewer 实现，但在内部使用 AsyncNetworkImage (它现在内部是 MioImage)
    // 或者，为了保持简单，维持现状，但里面的 AsyncNetworkImage 已经换成了 MioImage。
    // 下面是原有的手动实现，未做改动，因为它包含了手势和异步加载逻辑，MioImage 目前只接受已加载的 Painter。

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
                            } else { offsetX = 0f; offsetY = 0f }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismiss() },
                            onDoubleTap = { if (scale > 1f) { scale = 1f; offsetX = 0f; offsetY = 0f } else { scale = 2.5f } }
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