package com.github.nailkhaf.web3.abi

import com.github.nailkhaf.web3.keccak256
import com.github.nailkhaf.web3.models.Bytes4
import com.github.nailkhaf.web3.models.asBytes4
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import kotlin.reflect.KProperty

internal fun interface ParamsTransform<In> {
    operator fun invoke(value: In): List<AbiValue<*>>
}

internal fun interface ReturnsTransform<Out> {
    operator fun invoke(values: List<AbiValue<*>>): Out
}

class FunctionCall<In : Any, Out : Any> internal constructor(
    name: String,
    private val params: List<AbiType.Tuple.Param>,
    private val returns: List<AbiType.Tuple.Param>,
    private val paramsTransform: ParamsTransform<In>,
    private val returnsTransform: ReturnsTransform<Out>,
) {

    private val selector: Bytes4 by lazy {
        val signature = SignatureEncoder(StringBuilder(name)).run {
            appendTuple(params)
            string()
        }

        keccak256(signature.encodeUtf8()).bytes.substring(0, 4).asBytes4
    }

    fun encode(value: In): ByteString {
        val buffer = Buffer()
        buffer.write(selector.bytes)
        val encoder = AbiEncoder(buffer)
        encoder.encodeTuple(params, AbiValue.Tuple(paramsTransform(value)))
        return buffer.readByteString()
    }

    fun decode(source: ByteString): Out {
        val buffer = Buffer()
        buffer.write(source)
        val decoder = AbiDecoder(buffer)
        return returnsTransform(decoder.decodeTuple(returns).value)
    }
}

internal class FunctionCallDelegate<In : Any, Out : Any>(
    private val inputs: AbiTupleDsl.() -> Unit,
    private val outputs: AbiTupleDsl.() -> Unit,
    private val paramsTransform: ParamsTransform<In>,
    private val returnsTransform: ReturnsTransform<Out>,
) {

    private lateinit var value: FunctionCall<In, Out>

    operator fun getValue(any: Any, property: KProperty<*>): FunctionCall<In, Out> {
        return if (::value.isInitialized) {
            value
        } else {
            val inputs = AbiTupleDsl().apply(inputs).params
            val outputs = AbiTupleDsl().apply(outputs).params
            value = FunctionCall(property.name, inputs, outputs, paramsTransform, returnsTransform)
            value
        }
    }
}

@AbiDsl
internal fun <Out : Any> functionCall(
    @AbiDsl returns: AbiTupleDsl.() -> Unit,
    returnsTransform: ReturnsTransform<Out> = ReturnsTransform(::castFirst),
) = functionCall<Unit, Out>(
    params = {},
    paramsTransform = { emptyList() },
    returns = returns,
    returnsTransform = returnsTransform
)

@AbiDsl
internal fun <In : Any> functionCall(
    @AbiDsl params: AbiTupleDsl.() -> Unit,
    paramsTransform: ParamsTransform<In>,
) = functionCall(
    params = params,
    paramsTransform = paramsTransform,
    returns = {},
    returnsTransform = ::expectEmpty
)

@AbiDsl
internal fun <In : Any, Out : Any> functionCall(
    @AbiDsl params: AbiTupleDsl.() -> Unit,
    @AbiDsl returns: AbiTupleDsl.() -> Unit,
    paramsTransform: ParamsTransform<In>,
    returnsTransform: ReturnsTransform<Out>,
): FunctionCallDelegate<In, Out> = FunctionCallDelegate(
    params, returns, paramsTransform, returnsTransform
)

@Suppress("UNCHECKED_CAST")
internal fun <Out> castFirst(value: List<AbiValue<*>>): Out {
    check(value.size == 1) { "Wrong outputs transform, expected 1 element" }
    return value[0].value as Out
}

@Suppress("UNCHECKED_CAST")
internal fun expectEmpty(value: List<AbiValue<*>>) {
    check(value.isEmpty()) { "Wrong outputs transform, expected 0 element" }
}
