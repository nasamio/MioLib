import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 路由定义
 * 增加 index 字段用于手动控制菜单顺序
 */
sealed class Routes(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val index: Int // 这里的 index 数字决定了显示顺序，越小越靠前
) {

    object COMPONENTS : Routes("组件预览", "components", Icons.Default.Home, 0)
    object SMMS : Routes("我的图床", "smms", Icons.Default.ShoppingCart, 1)
    object SETTINGS : Routes("设置", "settings", Icons.Default.Settings, 2)
    object ABOUT : Routes("关于", "about", Icons.Default.AccountBox, 3)

    companion object {
        /**
         * 自动获取并根据 index 排序
         */
        val all: List<Routes> by lazy {
            Routes::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .sortedBy { it.index } // <--- 关键：获取后强制按 index 排序
        }

        fun getByRoute(route: String?): Routes? {
            return all.find { it.route == route }
        }
    }
}