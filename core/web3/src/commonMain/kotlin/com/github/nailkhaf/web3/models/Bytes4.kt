package com.github.nailkhaf.web3.models

import kotlinx.serialization.Serializable
import okio.ByteString
import okio.ByteString.Companion.toByteString

@JvmInline
@Serializable(Bytes4Serializer::class)
value class Bytes4(val bytes: ByteString) {
    init {
        check(bytes.size == 4)
    }

    companion object {
        val ZERO by lazy(LazyThreadSafetyMode.PUBLICATION) { ByteArray(4).toByteString().asBytes4 }
    }
}

val ByteString.asBytes4: Bytes4
    get() = Bytes4(this)
