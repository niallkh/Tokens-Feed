package com.github.nailkhaf.web3.abi

internal class SignatureEncoder(
    private val builder: StringBuilder = StringBuilder()
) {

    fun string() = builder.toString()

    fun appendType(type: AbiType, arrays: String = "") = when (type) {
        is AbiType.UNumber -> appendUInt(type.bits, arrays)
        is AbiType.Number -> appendInt(type.bits, arrays)
        is AbiType.Bool -> appendBool(arrays)
        is AbiType.FixedBytes -> appendFixedBytes(type.bytes, arrays)
        is AbiType.FixedAddress -> appendAddress(arrays)
        is AbiType.DynamicBytes -> appendDynamicBytes(arrays)
        is AbiType.DynamicString -> appendDynamicString(arrays)
        is AbiType.DynamicArray -> appendDynamicArray(type.param, arrays)
        is AbiType.FixedArray -> appendFixedArray(type.size, type.param, arrays)
        is AbiType.Tuple -> appendTuple(type.params, arrays)
    }

    private fun appendUInt(bits: UInt, arrays: String = "") {
        builder.append("uint${bits}")
        builder.append(arrays)
    }

    private fun appendInt(bits: UInt, arrays: String = "") {
        builder.append("int${bits}")
        builder.append(arrays)
    }

    private fun appendBool(arrays: String = "") {
        builder.append("bool")
        builder.append(arrays)
    }

    private fun appendAddress(arrays: String = "") {
        builder.append("address")
        builder.append(arrays)
    }

    private fun appendFixedBytes(bytes: UInt, arrays: String = "") {
        builder.append("bytes${bytes}")
        builder.append(arrays)
    }

    private fun appendDynamicBytes(arrays: String = "") {
        builder.append("bytes")
        builder.append(arrays)
    }

    private fun appendDynamicString(arrays: String = "") {
        builder.append("string")
        builder.append(arrays)
    }

    fun appendTuple(params: List<AbiType.Tuple.Param>, arrays: String = "") {
        builder.surroundBraces(items = params) {
            appendType(it.type, "")
        }
        builder.append(arrays)
    }

    private fun appendDynamicArray(param: AbiType, arrays: String = "") {
        appendType(param, "[]$arrays")
    }

    private fun appendFixedArray(size: UInt, param: AbiType, arrays: String = "") {
        appendType(param, "[${size}]$arrays")
    }
}

private inline fun <T> StringBuilder.surroundBraces(
    separator: String = ",",
    items: List<T>,
    block: (T) -> Unit,
): StringBuilder {
    append("(")
    items.forEachIndexed { index, item ->
        block(item)
        if (index != items.lastIndex) {
            append(separator)
        }
    }
    return append(")")
}