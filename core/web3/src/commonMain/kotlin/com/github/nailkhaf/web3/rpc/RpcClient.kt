package com.github.nailkhaf.web3.rpc

import io.ktor.http.*
import kotlinx.serialization.json.JsonElement

class RpcResponseException(message: String) : IllegalStateException(message)
class Web3StateUnavailableException : IllegalArgumentException()

interface RpcClient {

    suspend fun request(
        provider: Url,
        method: String,
        vararg params: JsonElement
    ): JsonElement
}
