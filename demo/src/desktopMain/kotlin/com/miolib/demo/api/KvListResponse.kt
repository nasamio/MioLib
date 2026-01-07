package com.miolib.demo.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class KvListResponse(
    val result: List<KvKey>,
    val success: Boolean,
    val errors: List<KvError>
)

@Serializable
data class KvKey(val name: String)

@Serializable
data class KvError(val message: String)

class CfKvClient(
    private val accountId: String,
    private val namespaceId: String,
    private val apiToken: String
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val baseUrl = "https://api.cloudflare.com/client/v4/accounts/$accountId/storage/kv/namespaces/$namespaceId"

    // 获取所有 Key
    suspend fun listKeys(): List<String> {
        val response = client.get("$baseUrl/keys") {
            header("Authorization", "Bearer $apiToken")
        }
        return if (response.status.isSuccess()) {
            val body: KvListResponse = response.body()
            body.result.map { it.name }
        } else emptyList()
    }

    // 读取 Value
    suspend fun getValue(key: String): String? {
        val response = client.get("$baseUrl/values/$key") {
            header("Authorization", "Bearer $apiToken")
        }
        return if (response.status.isSuccess()) response.bodyAsText() else null
    }

    // 写入/更新 Value
    suspend fun putValue(key: String, value: String): Boolean {
        val response = client.put("$baseUrl/values/$key") {
            header("Authorization", "Bearer $apiToken")
            setBody(value)
        }
        return response.status.isSuccess()
    }

    // 删除 Key
    suspend fun deleteKey(key: String): Boolean {
        val response = client.delete("$baseUrl/values/$key") {
            header("Authorization", "Bearer $apiToken")
        }
        return response.status.isSuccess()
    }
}