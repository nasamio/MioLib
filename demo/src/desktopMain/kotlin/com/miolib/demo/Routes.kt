package com.miolib.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Routes(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val index: Int,
) {
    object COMPONENTS : Routes("组件预览", "components", Icons.Default.Home, 0)
    object SMMS : Routes("我的图床", "smms", Icons.Default.ShoppingCart, 1)
    object R2 : Routes("R2 存储", "r2", Icons.Default.ShoppingCart, -1) // 新增 R2
    object RSS : Routes("RSS 阅读", "rss", Icons.Default.List, 3)
    object SETTINGS : Routes("设置", "settings", Icons.Default.Settings, 4)
    object ABOUT : Routes("关于", "about", Icons.Default.AccountBox, 5)
    object KV : Routes("KV 存储", "kv", Icons.Default.List, -3)
    object D1 : Routes("D1 存储", "d1", Icons.Default.List, -4)

    companion object {
        val all: List<Routes> by lazy {
            Routes::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .sortedBy { it.index }
        }

        fun getByRoute(route: String?): Routes? {
            return all.find { it.route == route }
        }
    }
}