package com.miolib.demo.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miolib.ui.components.*
import com.miolib.ui.theme.MioTheme
import com.miolib.ui.theme.state.MioThemeStyle
import com.miolib.ui.theme.state.MioThemeUtils
import kotlinx.coroutines.launch

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    MioCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.clip(MioTheme.shapes.cornerLarge)) {
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    currentStyle: MioThemeStyle,
    onStyleChange: (MioThemeStyle) -> Unit,
) {
    val scope = rememberCoroutineScope()

    // 模拟其他设置状态
    var airplaneMode by remember { mutableStateOf(false) }
    var wifiEnabled by remember { mutableStateOf(true) }
    var volume by remember { mutableStateOf(0.6f) }
    var brightness by remember { mutableStateOf(0.8f) }

    // 控制背景动画的开关 (这里仅仅为了演示，实际使用可以根据需求定死 true)
    var isBackgroundAnimated by remember { mutableStateOf(true) }

    // 此时背景色由 MioGridBackground 的底色决定 (modifier.background)
    // 而上面的网格线会自动适配当前主题的 Primary 颜色
    MioGridBackground(
        modifier = Modifier
            .fillMaxSize()
            .background(MioTheme.colors.background), // 底色
        isAnimated = isBackgroundAnimated
    ) {
        // 这里的 Box 是为了叠加右上角的控制按钮，如果是正式版可以去掉
        Box(Modifier.fillMaxSize()) {

            // 设置列表
            MioSettingsList(
                // [关键] 这里不再设置 background，保持透明以透出网格
                modifier = Modifier.fillMaxSize()
            ) {
                // --- 0. 主题风格 ---
                item {
                    MioSettingCategory("主题风格 (${MioThemeStyle.values().size})")
                }
                item {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 5
                    ) {
                        MioThemeStyle.values().forEach { style ->
                            ThemeOptionItem(
                                style = style,
                                isSelected = currentStyle == style,
                                onClick = { onStyleChange(style) }
                            )
                        }
                    }
                }

                // --- [演示] 背景动画开关 ---
                item {
                    MioSettingCategory("显示设置")
                }
                item {
                    SettingsCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MioSettingSwitch(
                            title = "动态背景",
                            subtitle = if (isBackgroundAnimated) "网格正在平移" else "网格已静止",
                            checked = isBackgroundAnimated,
                            onCheckedChange = { isBackgroundAnimated = it },
                            icon = Icons.Default.PlayArrow // 暂时用 Play 图标
                        )
                    }
                }

                // --- 1. 用户信息卡片 ---
                item {
                    MioSettingCategory("个人中心")
                }
                item {
                    SettingsCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { scope.launch { snackbarHostState.showSnackbar("进入个人详情页") } }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MioAvatar(
                                text = "M",
                                size = 56.dp,
                                backgroundColor = MioTheme.colors.primary,
                                textColor = MioTheme.colors.onPrimary
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                MioText("Mio User", style = MioTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                MioText("miouser@example.com", style = MioTheme.typography.caption)
                            }
                            Spacer(Modifier.weight(1f))
                            MioTag("PRO")
                        }
                    }
                }

                // --- 2. 网络设置 ---
                item {
                    MioSettingCategory("网络与连接")
                }
                item {
                    SettingsCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MioSettingSwitch(
                            title = "飞行模式",
                            subtitle = "禁用所有无线连接",
                            icon = Icons.Default.Share,
                            checked = airplaneMode,
                            onCheckedChange = { airplaneMode = it }
                        )
                        MioSettingSwitch(
                            title = "Wi-Fi",
                            subtitle = if (wifiEnabled) "已连接: MioHome_5G" else "已关闭",
                            icon = Icons.Default.Notifications,
                            checked = wifiEnabled,
                            onCheckedChange = { wifiEnabled = it },
                            enabled = !airplaneMode
                        )
                    }
                }

                // --- 3. 音效与显示 ---
                item {
                    MioSettingCategory("音效与显示")
                }
                item {
                    SettingsCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MioSettingSlider(
                            title = "媒体音量",
                            value = volume,
                            onValueChange = { volume = it },
                            icon = Icons.Default.Notifications
                        )
                        MioSettingSlider(
                            title = "屏幕亮度",
                            value = brightness,
                            onValueChange = { brightness = it },
                            icon = Icons.Default.Info
                        )
                    }
                }

                // --- 4. 通用 ---
                item {
                    MioSettingCategory("通用")
                }
                item {
                    SettingsCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MioSettingNavigation(
                            title = "关于软件",
                            icon = Icons.Default.Info,
                            info = "v1.2.0",
                            onClick = { scope.launch { snackbarHostState.showSnackbar("检查更新中...") } }
                        )
                        MioSettingItem(
                            title = "清除缓存",
                            icon = Icons.Default.Build,
                            subtitle = "占用 256MB",
                            onClick = { scope.launch { snackbarHostState.showSnackbar("缓存已清除") } },
                            trailingContent = {
                                MioText("清理", color = MioTheme.colors.primary, style = MioTheme.typography.label)
                            }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun ThemeOptionItem(
    style: MioThemeStyle,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val previewColors = remember(style) { MioThemeUtils.getColors(style) }

    val borderWidth by animateDpAsState(if (isSelected) 2.dp else 0.dp)
    val borderColor = MioTheme.colors.primary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 60.dp, height = 80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(previewColors.background)
                .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(previewColors.primary)
            )
            Box(
                modifier = Modifier
                    .padding(top = 32.dp, start = 6.dp, end = 6.dp)
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(previewColors.surface)
            )
            if (isSelected) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = previewColors.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        MioText(
            text = style.label,
            style = MioTheme.typography.caption,
            color = if (isSelected) MioTheme.colors.primary else MioTheme.colors.onSurface.copy(alpha = 0.6f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.dp.value.sp
        )
    }
}
