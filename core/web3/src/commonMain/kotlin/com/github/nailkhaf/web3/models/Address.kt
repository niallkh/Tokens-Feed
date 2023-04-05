package com.github.nailkhaf.web3.models

import com.github.nailkhaf.web3.keccak256
import kotlinx.serialization.Serializable
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString

@JvmInline
@Serializable(AddressSerializer::class)
value class Address(val bytes: ByteString) {

    init {
        check(bytes.size == 20)
    }

    companion object {
        val ZERO by lazy(LazyThreadSafetyMode.PUBLICATION) { ByteArray(20).toByteString().asAddress }
    }
}

val ByteString.asAddress: Address
    get() = Address(this)

fun String.decodeAddress(): Address = removePrefix("0x")
    .removePrefix("0X")
    .decodeHex()
    .asAddress

fun Address.formatChecksum(): String {
    val hex = bytes.hex()
    val hashedHex = keccak256(bytes.hex().encodeUtf8()).bytes.hex()

    return buildString(42) {
        append("0x")

        for ((char, hashedChar) in hex.zip(hashedHex)) {
            append(
                when {
                    char in '0'..'9' -> char
                    hashedChar in '0'..'7' -> char
                    else -> char.uppercaseChar()
                }
            )
        }
    }
}