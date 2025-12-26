package com.miolib.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miolib.ui.theme.MioTheme

/**
 * MioBadgeWrapper: 给组件添加右上角徽标
 *
 * @param count 数量 (如果是 null 则显示小红点，不显示数字)
 * @param content 被装饰的组件 (如 Icon)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MioBadgeWrapper(
    modifier: Modifier = Modifier,
    count: Int? = null,
    content: @Composable () -> Unit
) {
    BadgedBox(
        modifier = modifier,
        badge = {
            if (count != null) {
                Badge(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    MioText(
                        text = if (count > 99) "99+" else count.toString(),
                        style = MioTheme.typography.caption,
                        color = Color.White,
                        fontSize = MioTheme.sizes.small.fontSize // 借用小号字体
                    )
                }
            } else {
                // 纯红点模式
                Badge(
                    containerColor = Color.Red,
                    modifier = Modifier.size(8.dp)
                )
            }
        }
    ) {
        content()
    }
}