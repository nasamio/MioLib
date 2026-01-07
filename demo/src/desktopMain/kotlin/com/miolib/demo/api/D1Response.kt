package com.miolib.demo.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class D1Response(
    val result: List<D1Result>? = null,
    val success: Boolean = false,
    val errors: List<KvError> = emptyList()
)

@Serializable
data class D1Result(
    val results: JsonArray? = null, // 查询结果行
    val success: Boolean = false,
    val meta: JsonObject? = null
)

class CfD1Client(
    private val accountId: String,
    private val databaseId: String,
    private val apiToken: String
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val baseUrl = "https://api.cloudflare.com/client/v4/accounts/$accountId/d1/database/$databaseId"

    /**
     * 执行 SQL 查询
     * @param sql 完整的 SQL 语句
     * @param params 参数绑定列表 (可选)
     */
    suspend fun query(sql: String, params: List<JsonElement> = emptyList()): D1Response {
        return try {
            val response = client.post("$baseUrl/query") {
                header("Authorization", "Bearer ${apiToken.trim()}")
                header("Content-Type", "application/json")
                setBody(buildJsonObject {
                    put("sql", sql)
                    if (params.isNotEmpty()) {
                        put("params", JsonArray(params))
                    }
                })
            }
            if (!response.status.isSuccess()) {
                println("D1 Error: ${response.bodyAsText()}")
            }
            response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            D1Response(success = false)
        }
    }
}