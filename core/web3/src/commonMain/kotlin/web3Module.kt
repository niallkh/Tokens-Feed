package com.github.nailkhaf.web3

import com.github.nailkhaf.web3.models.AddressSerializer
import com.github.nailkhaf.web3.models.ByteStringSerializer
import com.github.nailkhaf.web3.models.Bytes32Serializer
import com.github.nailkhaf.web3.models.Bytes4Serializer
import com.github.nailkhaf.web3.rpc.DefaultRpcClient
import com.github.nailkhaf.web3.rpc.RpcClient
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.serializersModuleOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val web3Module = module {
    single {
        Json {
            serializersModule += SerializersModule {
                include(serializersModuleOf(ByteStringSerializer))
                include(serializersModuleOf(AddressSerializer))
                include(serializersModuleOf(Bytes32Serializer))
                include(serializersModuleOf(Bytes4Serializer))
            }
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }

    singleOf(::httpClientEngine)
    singleOf(::httpClient)
    singleOf(::DefaultRpcClient).bind<RpcClient>()
    single<NetworkRpcProvider> { StaticNetworkRpcProvider() }
    singleOf(::DefaultWeb3Provider).bind<Web3Provider>()
}

internal expect fun httpClientEngine(): HttpClientEngine

internal fun httpClient(
    json: Json,
    engine: HttpClientEngine
): HttpClient = HttpClient(engine) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(json)
    }

    install(ContentEncoding) {
        gzip()
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 20_000
        requestTimeoutMillis = 20_000
    }

    install(HttpRequestRetry) {
        retryOnException(5, retryOnTimeout = true)
        retryOnServerErrors(5)
        exponentialDelay()
    }
}