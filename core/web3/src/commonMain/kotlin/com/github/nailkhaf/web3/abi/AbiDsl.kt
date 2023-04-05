package com.github.nailkhaf.web3.abi


@DslMarker
internal annotation class AbiDsl

@AbiDsl
internal class AbiTupleDsl {
    val params: MutableList<AbiType.Tuple.Param> = mutableListOf()

    fun uint256(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.UNumber.UInt256, indexed = indexed, name = name)
    }

    fun uint32(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.UNumber.UInt32, indexed = indexed, name = name)
    }

    fun uint8(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.UNumber.UInt8, indexed = indexed, name = name)
    }

    fun int256(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.Number.Int256, indexed = indexed, name = name)
    }

    fun bytes4(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.FixedBytes.Bytes4, indexed = indexed, name = name)
    }

    fun bytes32(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.FixedBytes.Bytes32, indexed = indexed, name = name)
    }

    fun bytes(name: String? = null, indexed: Boolean = false, bytes: UInt? = null) {
        params += if (bytes == null) {
            AbiType.Tuple.Param(AbiType.DynamicBytes, indexed = indexed, name = name)
        } else {
            AbiType.Tuple.Param(AbiType.FixedBytes(bytes), indexed = indexed, name = name)
        }
    }

    fun address(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.FixedAddress, indexed = indexed, name = name)
    }

    fun string(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.DynamicString, indexed = indexed, name = name)
    }

    fun bool(name: String? = null, indexed: Boolean = false) {
        params += AbiType.Tuple.Param(AbiType.Bool, indexed = indexed, name = name)
    }

    fun tuple(
        name: String? = null,
        indexed: Boolean = false,
        @AbiDsl block: AbiTupleDsl.() -> Unit
    ) {
        val params = AbiTupleDsl().apply(block)
        this.params += AbiType.Tuple.Param(
            AbiType.Tuple(params = params.params),
            indexed = indexed,
            name = name
        )
    }

    fun array(
        name: String? = null,
        indexed: Boolean = false,
        size: UInt? = null,
        @AbiDsl block: AbiArrayDsl.() -> Unit
    ) {
        val type = AbiArrayDsl().apply(block)
        params += if (size == null) {
            AbiType.Tuple.Param(AbiType.DynamicArray(type.type), indexed = indexed, name = name)
        } else {
            AbiType.Tuple.Param(
                AbiType.FixedArray(size = size, param = type.type),
                indexed = indexed,
                name = name
            )
        }
    }
}

@AbiDsl
internal class AbiArrayDsl {
    lateinit var type: AbiType

    fun add(abiType: AbiType) {
        require(::type.isInitialized.not())
        type = abiType
    }

    fun uint256() {
        add(AbiType.UNumber.UInt256)
    }

    fun uint32() {
        add(AbiType.UNumber.UInt32)
    }

    fun uint8() {
        add(AbiType.UNumber.UInt8)
    }

    fun int256() {
        add(AbiType.Number.Int256)
    }

    fun bytes4() {
        add(AbiType.FixedBytes.Bytes4)
    }

    fun bytes32() {
        add(AbiType.FixedBytes.Bytes32)
    }

    fun bytes(bytes: UInt? = null) {
        if (bytes == null) {
            add(AbiType.DynamicBytes)
        } else {
            add(AbiType.FixedBytes(bytes))
        }
    }

    fun address() {
        add(AbiType.FixedAddress)
    }

    fun string() {
        add(AbiType.DynamicString)
    }

    fun bool() {
        add(AbiType.Bool)
    }

    fun tuple(@AbiDsl block: AbiTupleDsl.() -> Unit) {
        val params = AbiTupleDsl().apply(block)
        add(AbiType.Tuple(params = params.params))
    }

    fun array(
        size: UInt? = null,
        @AbiDsl block: AbiArrayDsl.() -> Unit
    ) {
        val type = AbiArrayDsl().apply(block)
        if (size == null) {
            add(AbiType.DynamicArray(type.type))
        } else {
            add(AbiType.FixedArray(size = size, param = type.type))
        }
    }
}

