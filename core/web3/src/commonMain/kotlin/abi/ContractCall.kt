package com.github.nailkhaf.web3.abi

import com.github.nailkhaf.web3.models.Address
import kotlinx.atomicfu.atomic
import okio.ByteString

data class ContractCall<In : Any, Out : Any>(
    val call: FunctionCall<In, Out>,
    internal val target: Address,
    private val value: In,
) {

    private var _result = atomic<Out?>(null)

    val resultOrNull: Out?
        get() = _result.value

    val result: Out
        get() = _result.value ?: error("Call failed")

    internal fun encode(): ByteString = call.encode(value)

    internal fun decode(source: ByteString) {
        _result.value = call.decode(source)
    }
}

fun <Out : Any> ContractCall(
    call: FunctionCall<Unit, Out>,
    target: Address,
) = ContractCall(
    call = call,
    target = target,
    value = Unit,
)

fun <In : Any, Out : Any> FunctionCall<In, Out>.contractCall(
    target: Address,
    value: In
) = ContractCall(
    call = this,
    target = target,
    value = value
)

fun <Out : Any> FunctionCall<Unit, Out>.contractCall(
    target: Address,
) = ContractCall(
    call = this,
    target = target,
    value = Unit
)