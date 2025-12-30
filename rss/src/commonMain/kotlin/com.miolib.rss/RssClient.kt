package com.miolib.rss

import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

data class RssItem(
    val title: String,
    val link: String,
    val pubDate: String,
    val description: String,
    val content: String
)

class RssClient {

    private val httpClient = HttpClient {
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    println("[RssClient] $message")
                }
            }
        }
    }

    suspend fun getRssFeed(url: String): List<RssItem> {
        return try {
            val response = httpClient.get(url) {
                // 1. 深度伪装成 Chrome 浏览器
                header(HttpHeaders.UserAgent, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                header(HttpHeaders.AcceptLanguage, "zh-CN,zh;q=0.9,en;q=0.8")
                header(HttpHeaders.CacheControl, "max-age=0")
                header("Sec-Ch-Ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                header("Sec-Ch-Ua-Mobile", "?0")
                header("Sec-Ch-Ua-Platform", "\"macOS\"")
                header("Sec-Fetch-Dest", "document")
                header("Sec-Fetch-Mode", "navigate")
                header("Sec-Fetch-Site", "none")
                header("Sec-Fetch-User", "?1")
                header("Upgrade-Insecure-Requests", "1")
            }

            // 2. 检查 HTTP 状态码
            if (response.status != HttpStatusCode.OK) {
                println("[RssClient] Error: ${response.status} from $url")
                // 如果是 403，通常是 Cloudflare 拦截，返回一个伪造的错误 Item 提示用户
                if (response.status == HttpStatusCode.Forbidden) {
                    return listOf(
                        RssItem(
                            title = "访问被拒绝 (403 Forbidden)",
                            link = url,
                            pubDate = "",
                            description = "目标服务器 ($url) 拒绝了请求。这通常是因为 RSSHub 的反爬虫策略。\n建议更换 RSSHub 实例域名（如自建或镜像）。",
                            content = "目标服务器 ($url) 拒绝了请求。<br>这通常是因为 RSSHub 的公共实例 (rsshub.app) 开启了 Cloudflare 高强度防护。<br><br><b>建议解决方案：</b><br>1. 使用自建的 RSSHub 实例。<br>2. 尝试其他未被滥用的公共镜像。<br>3. 稍后再试。"
                        )
                    )
                }
                return emptyList()
            }

            val xmlContent = response.bodyAsText()
            parseRssXml(xmlContent)

        } catch (e: Exception) {
            e.printStackTrace()
            // 网络错误兜底
            listOf(
                RssItem(
                    title = "网络请求失败",
                    link = url,
                    pubDate = "",
                    description = "错误信息: ${e.message}",
                    content = "请求 URL: $url<br>错误堆栈:<br>${e.stackTraceToString()}"
                )
            )
        }
    }

    private suspend fun parseRssXml(xml: String): List<RssItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<RssItem>()
        // 匹配 <item>...</item>
        val itemRegex = "<item[\\s\\S]*?>([\\s\\S]*?)</item>".toRegex(RegexOption.IGNORE_CASE)

        // 1. 强大的反转义函数 (支持 &#34; &#x2F; 等)
        fun unescapeHtml(input: String): String {
            var text = input
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&nbsp;", " ")

            // 处理数字实体 &#34; -> "
            val decimalRegex = Regex("&#(\\d+);")
            text = decimalRegex.replace(text) { match ->
                try {
                    match.groupValues[1].toInt().toChar().toString()
                } catch (e: Exception) { match.value }
            }

            // 处理十六进制实体 &#x2F; -> /
            val hexRegex = Regex("&#x([0-9a-fA-F]+);")
            text = hexRegex.replace(text) { match ->
                try {
                    match.groupValues[1].toInt(16).toChar().toString()
                } catch (e: Exception) { match.value }
            }
            return text
        }

        // 2. 提取 CData 或 文本
        fun extractRawText(tagPattern: String, source: String): String {
            val regex = "<$tagPattern[^>]*>([\\s\\S]*?)</$tagPattern>".toRegex(RegexOption.IGNORE_CASE)
            val raw = regex.find(source)?.groupValues?.getOrNull(1) ?: return ""

            val cdataRegex = "<!\\[CDATA\\[([\\s\\S]*?)]]>".toRegex(RegexOption.IGNORE_CASE)
            val cdataMatch = cdataRegex.find(raw)
            if (cdataMatch != null) {
                return cdataMatch.groupValues[1]
            }
            return raw
        }

        // 3. 彻底清洗 HTML
        fun cleanHtml(html: String): String {
            if (html.isEmpty()) return ""
            var text = html

            // 第一轮反转义
            text = unescapeHtml(text)

            // 结构化处理
            text = text.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
            text = text.replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n\n")
            text = text.replace(Regex("</div>", RegexOption.IGNORE_CASE), "\n")
            text = text.replace(Regex("</li>", RegexOption.IGNORE_CASE), "\n")

            // 移除特定干扰文案
            text = text.replace(Regex("查看全文"), "")

            // 移除所有 HTML 标签
            text = text.replace(Regex("<[^>]*>"), "")

            // 第二轮反转义
            text = unescapeHtml(text)

            // 整理空行
            text = text.replace(Regex("\n\\s*\n\\s*\n+"), "\n\n")

            return text.trim()
        }

        itemRegex.findAll(xml).forEach { matchResult ->
            val itemContent = matchResult.groupValues[1]

            val title = cleanHtml(extractRawText("title", itemContent))
            val link = extractRawText("link", itemContent).trim()
            val pubDate = extractRawText("pubDate", itemContent).trim()

            var rawContent = extractRawText("content:encoded", itemContent)
            if (rawContent.isBlank()) rawContent = extractRawText("content", itemContent)
            if (rawContent.isBlank()) rawContent = extractRawText("body", itemContent)
            val rawDesc = extractRawText("description", itemContent)

            val cleanContent = cleanHtml(rawContent)
            val cleanDesc = cleanHtml(rawDesc)

            val finalContent = if (cleanContent.length > cleanDesc.length + 50) cleanContent else cleanDesc

            items.add(
                RssItem(
                    title = title,
                    link = link,
                    pubDate = pubDate,
                    description = cleanDesc.take(100).replace("\n", " ") + "...",
                    content = finalContent
                )
            )
        }
        items
    }
}