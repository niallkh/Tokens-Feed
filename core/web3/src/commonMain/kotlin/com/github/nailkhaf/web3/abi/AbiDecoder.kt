package com.github.nailkhaf.web3.abi

import com.github.nailkhaf.web3.models.asAddress
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import okio.BufferedSource

internal class AbiDecoder(
    private val source: BufferedSource
) {

    fun decode(type: AbiType): AbiValue<*> = when (type) {
        is AbiType.UNumber -> decodeUInt(type.bits)
        is AbiType.Number -> decodeInt(type.bits)
        is AbiType.Bool -> decodeBool()
        is AbiType.FixedAddress -> decodeAddress()
        is AbiType.FixedBytes -> decodeFixedBytes(type.bytes)
        is AbiType.DynamicString -> decodeDynamicString()
        is AbiType.DynamicBytes -> decodeDynamicBytes()
        is AbiType.DynamicArray -> decodeDynamicArray(type.param)
        is AbiType.FixedArray -> decodeFixedArray(type.size, type.param)
        is AbiType.Tuple -> decodeTuple(type.params)
    }

    private fun decodeUInt(bits: UInt): AbiValue.Number {
        require(bits in 8u..256u && bits % 8u == 0u)
        source.skip((32u - bits / 8u).toLong())
        return BigInteger.fromByteArray(source.readByteArray(bits.toLong() / 8L), Sign.POSITIVE)
            .let { AbiValue.Number(it) }
    }

    private fun decodeInt(bits: UInt): AbiValue.Number {
        require(bits in 8u..256u && bits % 8u == 0u)
        source.skip((32u - bits / 8u).toLong())
        return BigInteger.fromTwosComplementByteArray(source.readByteArray(bits.toLong() / 8L))
            .let { AbiValue.Number(it) }
    }

    private fun decodeBool(): AbiValue.Bool {
        source.skip(31)
        return when (source.readByte()) {
            0.toByte() -> false
            else -> true
        }
            .let { AbiValue.Bool(it) }
    }

    private fun decodeAddress(): AbiValue.FixedAddress {
        source.skip(12)
        return source.readByteString(20).asAddress
            .let { AbiValue.FixedAddress(it) }
    }

    private fun decodeFixedBytes(bytes: UInt): AbiValue.Bytes {
        require(bytes in 1u..32u)
        return source.readByteString(bytes.toLong()).also {
            source.skip(32L - bytes.toLong())
        }
            .let { AbiValue.Bytes(it) }
    }

    private fun decodeDynamicBytes(): AbiValue.Bytes {
        val size = decodeUInt(256u).value.longValue(exactRequired = true)
        return source.readByteString(size).also {
            val padding = size % 32L
            if (padding > 0) {
                source.skip(32L - padding)
            }
        }
            .let { AbiValue.Bytes(it) }
    }

    private fun decodeDynamicString(): AbiValue.DynamicString {
        return decodeDynamicBytes().value.utf8()
            .let { AbiValue.DynamicString(it) }
    }

    private fun decodeDynamicArray(param: AbiType): AbiValue.Array<*> {
        val size = decodeUInt(256u).value.uintValue(true)
        return if (size != 0u) {
            decodeFixedArray(size, param).value
        } else {
            emptyList()
        }
            .let { AbiValue.Array(it) }
    }

    private fun decodeFixedArray(size: UInt, param: AbiType): AbiValue.Array<*> {
        if (isDynamic(param)) {
            source.skip(size.toLong() * 32)
        }

        return (0u until size).map {
            decode(param)
        }
            .let { AbiValue.Array(it) }
    }

    fun decodeTuple(params: List<AbiType.Tuple.Param>): AbiValue.Tuple {
        val heads = params.map { param ->
            if (isDynamic(param.type)) {
                source.skip(32)
                null
            } else {
                decode(param.type)
            }
        }

        val tails = params.map { param ->
            if (isDynamic(param.type)) {
                decode(param.type)
            } else {
                null
            }
        }

        return List(params.size) { index ->
            heads[index] ?: tails[index]!!
        }
            .let { AbiValue.Tuple(it) }
    }
}
