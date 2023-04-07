package com.github.nailkhaf.web3.rpc

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class DefaultRpcClient(
    private val httpClient: HttpClient,
) : RpcClient {

    override suspend fun request(
        provider: Url,
        method: String,
        vararg params: JsonElement
    ): JsonElement {
        val rpcRequest = RpcRequest(
            jsonrpc = "2.0",
            id = 0,
            method = method,
            params = params.toList()
        )

        val statement = httpClient.preparePost {
            url(provider)
            setBody(rpcRequest)
            contentType(ContentType.Application.Json)
        }

        val response = withContext(Dispatchers.IO) {
            statement.execute()
        }

        val rpcResponse = response.body<RpcResponse>()

        rpcResponse.error?.let {
            val message = it.jsonObject
                .getOrDefault("message", JsonPrimitive("RPC return error"))
                .jsonPrimitive.toString()
            throw RpcResponseException(message)
        }

        return rpcResponse.result ?: throw Web3StateUnavailableException()
    }
}
