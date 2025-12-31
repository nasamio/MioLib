package com.miolib.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

/**
 * MioCard: 通用卡片组件
 * [修复] 恢复了旧参数以兼容 MioDialog，并修正了 Theme 引用错误
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MioCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    // --- 恢复旧参数以兼容现有代码 ---
    backgroundColor: Color = MioTheme.colors.surface,
    contentColor: Color = MioTheme.colors.onSurface,
    elevation: Dp = 4.dp,
    hasBorder: Boolean = false,
    // --- 新增参数 ---
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    // 1. 恢复边框逻辑
    val borderStroke = if (hasBorder) {
        BorderStroke(1.dp, MioTheme.colors.outline.copy(alpha = 0.2f))
    } else null

    // 2. 修正形状引用 (MioTheme 中是 cornerLarge/cornerMedium，不是 medium)
    // 根据之前的代码，Card 通常使用 Large
    val shape = MioTheme.shapes.cornerLarge

    // 3. 构建颜色
    val colors = CardDefaults.cardColors(
        containerColor = backgroundColor,
        contentColor = contentColor
    )

    // 4. 构建阴影
    val elevationObj = CardDefaults.cardElevation(defaultElevation = elevation)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        border = borderStroke,
        colors = colors,
        elevation = elevationObj,
        onClick = onClick ?: {} // 如果 onClick 为 null，Card 组件本身不响应点击，但为了复用 Card 参数，这里做一个空处理或者使用下面的非 onClick 重载
    ) {
        // 注意：Material3 的 Card(onClick=...) 只有在 onClick != null 时才应该调用
        // 但为了简化代码结构，这里我们统一使用 modifier.clickable 来处理点击（如果 Card 原生 onClick 行为不一致的话）
        // 不过，为了保持最佳 Material 体验，应该区分处理：
    }

    // 为了避免上面的 onClick 逻辑复杂化，我们回退到使用最稳健的写法：
    // 根据是否有 onClick 选择不同的 Card 重载或 Modifier

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            border = borderStroke,
            colors = colors,
            elevation = elevationObj
        ) {
            Column(modifier = Modifier.padding(contentPadding)) { // 使用传入的 padding
                content()
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            border = borderStroke,
            colors = colors,
            elevation = elevationObj
        ) {
            Column(modifier = Modifier.padding(contentPadding)) { // 使用传入的 padding
                content()
            }
        }
    }
}