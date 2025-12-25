package com.miolib.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miolib.ui.components.MioButton
import com.miolib.ui.theme.DarkColors
import com.miolib.ui.theme.MioTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun TestMioComponents() {
    Column {
        // 1. 测试默认主题效果
        MioTheme {
            Column(
                modifier = Modifier
                    .background(MioTheme.colors.background)
                    .padding(20.dp)
            ) {
                MioButton(
                    text = "默认风格按钮",
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. 测试局部自定义 (红色警告按钮)
                MioButton(
                    text = "自定义红色按钮",
                    onClick = {},
                    backgroundColor = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. 测试暗黑模式效果 (统一修改整体样式)
        MioTheme(colors = DarkColors) {
            Column(
                modifier = Modifier
                    .background(MioTheme.colors.background)
                    .padding(20.dp)
            ) {
                MioButton(
                    text = "暗黑模式按钮",
                    onClick = {}
                )
            }
        }
    }
}