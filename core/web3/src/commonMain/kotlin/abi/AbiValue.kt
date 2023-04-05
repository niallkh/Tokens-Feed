package com.github.nailkhaf.web3.abi

import com.github.nailkhaf.web3.models.Address
import com.ionspin.kotlin.bignum.integer.BigInteger
import okio.ByteString

sealed interface AbiValue<T> {
    val value: T
    operator fun invoke(): T = value

    @JvmInline
    value class Number(override val value: BigInteger) : AbiValue<BigInteger>

    @JvmInline
    value class Bool(override val value: Boolean) : AbiValue<Boolean>

    @JvmInline
    value class FixedAddress(override val value: Address) : AbiValue<Address>

    @JvmInline
    value class Bytes(override val value: ByteString) : AbiValue<ByteString>

    @JvmInline
    value class DynamicString(override val value: String) : AbiValue<String>

    @JvmInline
    value class Array<T : AbiValue<*>>(override val value: List<T>) : AbiValue<List<T>>

    @JvmInline
    value class Tuple(override val value: List<AbiValue<*>>) : AbiValue<List<AbiValue<*>>>
}


internal val BigInteger.abi
    get() = AbiValue.Number(this)
internal val ByteString.abi
    get() = AbiValue.Bytes(this)
internal val Boolean.abi
    get() = AbiValue.Bool(this)
internal val String.abi
    get() = AbiValue.DynamicString(this)
internal val Address.abi
    get() = AbiValue.FixedAddress(this)
internal val List<AbiValue<*>>.tuple
    get() = AbiValue.Tuple(this)
internal val List<AbiValue<*>>.array
    get() = AbiValue.Array(this)