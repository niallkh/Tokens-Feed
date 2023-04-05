package com.github.nailkhaf.web3.abi

sealed interface AbiType {

    class UNumber(val bits: UInt) : AbiType {

        companion object {
            val UInt256 = UNumber(256u)
            val UInt32 = UNumber(32u)
            val UInt8 = UNumber(8u)
        }
    }

    class Number(val bits: UInt) : AbiType {

        companion object {
            val Int256 = Number(256u)
        }
    }

    object Bool : AbiType

    class FixedBytes(val bytes: UInt) : AbiType {

        companion object {
            val Bytes32 = FixedBytes(32u)
            val Bytes4 = FixedBytes(4u)
        }
    }

    object FixedAddress : AbiType

    object DynamicString : AbiType

    object DynamicBytes : AbiType

    class DynamicArray(val param: AbiType) : AbiType

    class FixedArray(val size: UInt, val param: AbiType) : AbiType

    class Tuple(val params: List<Param>) : AbiType {
        data class Param(
            val type: AbiType,
            val indexed: Boolean,
            val name: String? = null,
        )
    }
}

internal fun isDynamic(param: AbiType): Boolean = when (param) {
    is AbiType.UNumber,
    is AbiType.Number,
    is AbiType.Bool,
    is AbiType.FixedBytes,
    is AbiType.FixedAddress,
    -> false

    is AbiType.DynamicString -> true
    is AbiType.DynamicBytes -> true
    is AbiType.DynamicArray -> true
    is AbiType.FixedArray -> param.param.let(::isDynamic)
    is AbiType.Tuple -> param.params.any { isDynamic(it.type) }
}