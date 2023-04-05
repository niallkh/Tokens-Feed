package com.github.nailkhaf.web3.models

import kotlinx.serialization.Serializable
import okio.ByteString
import okio.ByteString.Companion.toByteString

@JvmInline
@Serializable(Bytes32Serializer::class)
value class Bytes32(val bytes: ByteString) {
    init {
        check(bytes.size == 32)
    }

    companion object {
        val ZERO by lazy(LazyThreadSafetyMode.PUBLICATION) { ByteArray(32).toByteString().asBytes32 }
    }
}

val ByteString.asBytes32: Bytes32
    get() = Bytes32(this)

val Bytes32.first4Bytes: Bytes4
    get() = bytes.substring(0, 4).asBytes4