package com.github.nailkhaf.web3.models

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okio.ByteString
import okio.ByteString.Companion.decodeHex


object ByteStringSerializer : KSerializer<ByteString> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ByteString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteString) {
        encoder.encodeString("0x${value.hex()}")
    }

    override fun deserialize(decoder: Decoder): ByteString {
        return decoder.decodeString()
            .removePrefix("0x")
            .removePrefix("0X")
            .decodeHex()
    }
}

object Bytes32Serializer : KSerializer<Bytes32> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Bytes32", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Bytes32) {
        ByteStringSerializer.serialize(encoder, value = value.bytes)
    }

    override fun deserialize(decoder: Decoder): Bytes32 {
        return ByteStringSerializer.deserialize(decoder).asBytes32
    }
}

object Bytes4Serializer : KSerializer<Bytes4> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Bytes4", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Bytes4) {
        ByteStringSerializer.serialize(encoder, value = value.bytes)
    }

    override fun deserialize(decoder: Decoder): Bytes4 {
        return ByteStringSerializer.deserialize(decoder).asBytes4
    }
}

object AddressSerializer : KSerializer<Address> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Address", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Address) {
        ByteStringSerializer.serialize(encoder, value = value.bytes)
    }

    override fun deserialize(decoder: Decoder): Address {
        return ByteStringSerializer.deserialize(decoder).asAddress
    }
}

object BigIntegerSerializer : KSerializer<BigInteger> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeString("0x${value.toString(16)}")
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        return decoder.decodeString()
            .removePrefix("0x")
            .removePrefix("0X")
            .toBigInteger(16)
    }
}

