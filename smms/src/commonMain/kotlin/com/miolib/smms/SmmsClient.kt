package com.miolib.smms

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// --- 数据模型 ---

@Serializable
data class SmmsResponse(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: SmmsData? = null,
    @SerialName("RequestId") val requestId: String? = null,
)

@Serializable
data class SmmsData(
    val fileId: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val filename: String = "",
    val storename: String = "",
    val size: Int = 0,
    val path: String = "",
    val hash: String = "",
    val url: String = "",
    val delete: String = "",
    val page: String = "",
)

// --- Client 实现 ---

class SmmsClient(private val token: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
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

    /**
     * 上传图片
     * @param fileName 文件名
     * @param fileBytes 图片字节数据
     */
    suspend fun uploadImage(fileName: String, fileBytes: ByteArray): SmmsResponse {
        return try {
            val response = client.post("https://sm.ms/api/v2/upload") {
                header("Authorization", token) // SM.MS 需要 Token

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
            }
            response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            SmmsResponse(
                success = false,
                code = "error",
                message = e.message ?: "Unknown error"
            )
        }
    }
}