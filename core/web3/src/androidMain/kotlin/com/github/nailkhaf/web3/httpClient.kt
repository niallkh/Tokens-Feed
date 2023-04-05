package com.github.nailkhaf.web3

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

internal actual fun httpClientEngine(): HttpClientEngine = OkHttp.create {
    config {
        followRedirects(true)
    }
}