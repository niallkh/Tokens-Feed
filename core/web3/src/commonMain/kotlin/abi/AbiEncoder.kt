package com.github.nailkhaf.web3.abi

import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.ionspin.kotlin.bignum.integer.util.toTwosComplementByteArray
import okio.BufferedSink
import okio.ByteString.Companion.encodeUtf8
import okio.utf8Size
import kotlin.math.ceil

internal class AbiEncoder(
    private val sink: BufferedSink
) {

    fun encode(type: AbiType, value: AbiValue<*>): Unit = when (type) {
        is AbiType.UNumber -> encodeUInt(type.bits, value as AbiValue.Number)
        is AbiType.Number -> encodeInt(type.bits, value as AbiValue.Number)
        is AbiType.Bool -> encodeBool(value as AbiValue.Bool)
        is AbiType.FixedAddress -> encodeAddress(value as AbiValue.FixedAddress)
        is AbiType.FixedBytes -> encodeFixedBytes(type.bytes, value as AbiValue.Bytes)
        is AbiType.DynamicBytes -> encodeDynamicBytes(value as AbiValue.Bytes)
        is AbiType.DynamicString -> encodeDynamicString(value as AbiValue.DynamicString)
        is AbiType.DynamicArray -> encodeDynamicArray(type.param, value as AbiValue.Array<*>)
        is AbiType.FixedArray -> encodeFixedArray(type.size, type.param, value as AbiValue.Array<*>)
        is AbiType.Tuple -> encodeTuple(type.params, value as AbiValue.Tuple)
    }

    private fun encodeUInt(bits: UInt, value: AbiValue.Number) {
        require(bits in 8u..256u && bits % 8u == 0u)
        require(value().getSign() != Sign.NEGATIVE)

        val byteArray = value().toByteArray()
        require(byteArray.size.toUInt() <= bits / 8u)

        sink.padding(32 - byteArray.size)
        sink.write(byteArray)
    }

    private fun encodeInt(bits: UInt, value: AbiValue.Number) {
        require(bits in 8u..256u && bits % 8u == 0u)

        val byteArray = value().toTwosComplementByteArray()
        require(byteArray.size.toUInt() <= bits / 8u)

        sink.padding(
            32 - byteArray.size,
            byte = if (value().getSign() != Sign.NEGATIVE) 0 else 0xff
        )
        sink.write(byteArray)
    }

    private fun encodeBool(value: AbiValue.Bool) {
        sink.padding(31)
        sink.writeByte(if (value()) 1 else 0)
    }

    private fun encodeAddress(value: AbiValue.FixedAddress) {
        sink.padding(12)
        sink.write(value().bytes)
    }

    private fun encodeFixedBytes(bytes: UInt, value: AbiValue.Bytes) {
        require(bytes in 1u..32u)
        require(value().size.toUInt() == bytes)
        sink.write(value())
        sink.padding(32 - value().size)
    }

    private fun encodeDynamicBytes(value: AbiValue.Bytes) {
        encodeUInt(256u, value().size.toBigInteger().abi)
        sink.write(value())

        val rest = value().size % 32
        if (rest > 0) {
            sink.padding(32 - rest)
        }
    }

    private fun encodeDynamicString(value: AbiValue.DynamicString) {
        encodeDynamicBytes(value().encodeUtf8().abi)
    }

    private fun encodeDynamicArray(param: AbiType, value: AbiValue.Array<*>) {
        encodeUInt(256u, value().size.toBigInteger().abi)
        if (value().isNotEmpty()) {
            encodeFixedArray(value().size.toUInt(), param, value().array)
        }
    }

    private fun encodeFixedArray(size: UInt, param: AbiType, value: AbiValue.Array<*>) {
        require(value().size.toUInt() == size)

        val dynamic = isDynamic(param)
        var offset = value().size.toUInt() * 32u

        value().forEach { childValue ->
            if (dynamic.not()) {
                encode(param, childValue)
            } else {
                encodeUInt(256u, offset.toBigInteger().abi)
                offset += estimateLength(param, childValue)
            }
        }

        if (dynamic) {
            value().forEach { childValue -> encode(param, childValue) }
        }
    }

    fun encodeTuple(params: List<AbiType.Tuple.Param>, value: AbiValue.Tuple) {
        require(params.size == value().size)

        var offset = value().size.toUInt() * 32u

        params.zip(value()).forEach { (param, value) ->
            if (isDynamic(param.type).not()) {
                encode(param.type, value)
            } else {
                encodeUInt(256u, offset.toBigInteger().abi)
                offset += estimateLength(param.type, value)
            }
        }

        params.zip(value()).forEach { (param, value) ->
            if (isDynamic(param.type)) {
                encode(param.type, value)
            }
        }
    }

    private fun estimateLength(type: AbiType, value: AbiValue<*>): UInt = when (type) {
        is AbiType.UNumber -> 32u
        is AbiType.Number -> 32u
        is AbiType.Bool -> 32u
        is AbiType.FixedAddress -> 32u
        is AbiType.FixedBytes -> 32u
        is AbiType.DynamicBytes -> estimateLengthDynamicBytes(value as AbiValue.Bytes)
        is AbiType.DynamicString -> estimateLengthDynamicString(value as AbiValue.DynamicString)
        is AbiType.DynamicArray -> estimateLengthDynamicArray(
            type.param,
            value as AbiValue.Array<*>
        )
        is AbiType.FixedArray -> estimateLengthFixedArray(
            type.size,
            type.param,
            value as AbiValue.Array<*>
        )
        is AbiType.Tuple -> estimateLengthTuple(type.params, value as AbiValue.Tuple)
    }

    private fun estimateLengthDynamicBytes(value: AbiValue.Bytes): UInt {
        return 32u + ceil(value().size / 32f).toUInt() * 32u
    }

    private fun estimateLengthDynamicString(value: AbiValue.DynamicString): UInt {
        return 32u + ceil(value().utf8Size() / 32f).toUInt() * 32u
    }

    private fun estimateLengthDynamicArray(param: AbiType, value: AbiValue.Array<*>): UInt {
        return 32u + if (value().isNotEmpty()) {
            estimateLengthFixedArray(size = value().size.toUInt(), param, value().array)
        } else {
            0u
        }
    }

    private fun estimateLengthFixedArray(
        size: UInt,
        param: AbiType,
        value: AbiValue.Array<*>
    ): UInt {
        require(value().size.toUInt() == size)

        val head = value().size.toUInt() * 32u

        val tail = if (isDynamic(param)) {
            value().sumOf { estimateLength(param, it) }
        } else {
            0u
        }

        return head + tail
    }

    private fun estimateLengthTuple(
        params: List<AbiType.Tuple.Param>,
        value: AbiValue.Tuple
    ): UInt {
        require(params.size == value().size)

        val head = value().size.toUInt() * 32u

        val tail = params.zip(value()).fold(0u) { acc, (param, value) ->
            if (isDynamic(param.type)) acc + estimateLength(param.type, value) else acc
        }

        return head + tail
    }
}

private fun BufferedSink.padding(size: Int, byte: Int = 0) {
    repeat(size) {
        writeByte(byte)
    }
}
