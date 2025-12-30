package com.miolib.rss

/**
 * RSS 订阅源数据模型
 */
data class RssSource(
    val name: String,
    val url: String
)

/**
 * 默认配置
 */
object RssDefaults {
    val host = "http://rsshub.isrss.com"
    val defaultFeeds = listOf(
        RssSource("知乎专栏", "https://rsshub.app/baoyu/blog"),
        RssSource("RSSHub", "https://rsshub.app/xiaohongshu/user/593032945e87e77791e03696/notes"),
        RssSource("awesomeRSSHub", "http://rsshub.isrss.com/telegram/channel/awesomeRSSHub"),
    ).map {
        it.copy(url = it.url.replace("https://rsshub.app", host))
    }
}