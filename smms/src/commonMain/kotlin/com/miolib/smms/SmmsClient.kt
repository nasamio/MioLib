package com.miolib.smms

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// --- 基础响应 ---
@Serializable
open class BaseSmmsResponse {
    val success: Boolean = false
    val code: String = ""
    val message: String = ""
    @SerialName("RequestId") val requestId: String? = null
}

// --- 1. 删除/通用 响应 ---
@Serializable
class SmmsBasicResponse : BaseSmmsResponse()

// --- 2. 上传 响应 ---
@Serializable
data class SmmsUploadResponse(
    val data: SmmsData? = null
) : BaseSmmsResponse()

// --- 3. 历史记录 响应 ---
@Serializable
data class SmmsHistoryResponse(
    val data: List<SmmsData>? = null
) : BaseSmmsResponse()

// --- 4. 用户信息 响应 ---
@Serializable
data class SmmsProfileResponse(
    val data: SmmsProfileData? = null
) : BaseSmmsResponse()

// --- 数据模型 ---
@Serializable
data class SmmsData(
    @SerialName("file_id") val fileId: Int? = 0,
    val width: Int? = 0,
    val height: Int? = 0,
    val filename: String? = "",
    val storename: String? = "",
    val size: Int? = 0,
    val path: String? = "",
    val hash: String? = "",
    val url: String? = "",
    val delete: String? = "",
    val page: String? = "",
    // 修正：实际 API 返回的是 "yyyy-MM-dd HH:mm:ss" 字符串，而非文档示例中的 Long 时间戳
    @SerialName("created_at") val createdAt: String? = null,
    val ip: String? = null
)

@Serializable
data class SmmsProfileData(
    val username: String = "",
    val email: String = "",
    val role: String = "",
    @SerialName("disk_usage") val diskUsage: String = "",
    @SerialName("disk_limit") val diskLimit: String = "",
    @SerialName("disk_usage_raw") val diskUsageRaw: Long = 0,
    @SerialName("disk_limit_raw") val diskLimitRaw: Long = 0
)

// --- Client 实现 ---
class SmmsClient(private val token: String) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    println("[SmmsClient] $message")
                }
            }
        }
    }

    suspend fun getProfile(): SmmsProfileResponse {
        return try {
            httpClient.post("https://sm.ms/api/v2/profile") {
                header("Authorization", token)
            }.body()
        } catch (e: Exception) {
            println("GetProfile Error: ${e.message}")
            SmmsProfileResponse()
        }
    }

    suspend fun uploadImage(fileName: String, fileBytes: ByteArray): SmmsUploadResponse {
        return try {
            httpClient.post("https://sm.ms/api/v2/upload") {
                header("Authorization", token)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("smfile", fileBytes, Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                            })
                            append("format", "json")
                        }
                    )
                )
            }.body()
        } catch (e: Exception) {
            println("Upload Error: ${e.message}")
            SmmsUploadResponse()
        }
    }

    suspend fun getHistory(): SmmsHistoryResponse {
        return try {
            httpClient.get("https://sm.ms/api/v2/upload_history") {
                header("Authorization", token)
            }.body()
        } catch (e: Exception) {
            println("GetHistory Error: ${e.message}")
            e.printStackTrace()
            SmmsHistoryResponse()
        }
    }

    suspend fun deleteImage(hash: String): SmmsBasicResponse {
        return try {
            httpClient.get("https://sm.ms/api/v2/delete/$hash") {
                header("Authorization", token)
            }.body()
        } catch (e: Exception) {
            println("Delete Error: ${e.message}")
            SmmsBasicResponse()
        }
    }

    suspend fun downloadImage(url: String): ByteArray? {
        return try {
            val response: HttpResponse = httpClient.get(url)
            response.readRawBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}