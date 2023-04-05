package com.github.nailkhaf.web3.abi

import com.github.nailkhaf.web3.keccak256
import com.github.nailkhaf.web3.models.Bytes32
import com.github.nailkhaf.web3.models.asBytes32
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import kotlin.reflect.KProperty


class EventLog<T : Any, F : Any> internal constructor(
    private val name: String,
    private val params: List<AbiType.Tuple.Param>,
    private val paramsTransform: (List<AbiValue<*>>) -> T,
    private val filterTransform: (F) -> List<AbiValue<*>?>,
    private val anonymous: Boolean = false
) {

    val signature by lazy {
        val signatureEncoder = SignatureEncoder(StringBuilder(name))
        signatureEncoder.appendTuple(params)
        val sigName = signatureEncoder.string()
        keccak256(sigName.encodeUtf8())
    }

    fun decode(
        topics: List<Bytes32>,
        data: ByteString,
    ): T {
        val fixedTopics = if (anonymous) topics
        else topics.drop(1)

        val indexedParams = params.filter { it.indexed }
        require((fixedTopics.size) == indexedParams.size) {
            "Topics and indexed params have different size"
        }
        val decodedTopics = indexedParams.zip(fixedTopics) { param, topic ->
            val buffer = Buffer().apply { write(topic.bytes) }
            // TODO handle dynamic as hash
            AbiDecoder(buffer).decode(param.type)
        }

        val buffer = Buffer().apply { write(data) }
        val decoder = AbiDecoder(buffer)
        val decodedParams = decoder.decodeTuple(params.filter { it.indexed.not() }).value
        return paramsTransform(decodedTopics + decodedParams)
    }

    fun encodeTopics(filter: F): List<Bytes32?> {
        val topics = filterTransform(filter)
        val indexedParams = params.filter { it.indexed }
        require(topics.size == indexedParams.size) {
            "Topics and indexed params have different size"
        }

        val topicFilters = indexedParams.zip(topics).map { (param, topic) ->
            if (topic != null) {
                val buffer = Buffer()
                AbiEncoder(buffer).encode(param.type, topic)
                buffer.readByteString().asBytes32
            } else {
                null
            }
        }

        return if (anonymous) {
            topicFilters
        } else {
            listOf(signature) + topicFilters
        }
    }
}

internal class EventLogDelegate<In : Any, F : Any>(
    private val params: AbiTupleDsl.() -> Unit,
    private val paramsTransform: (List<AbiValue<*>>) -> In,
    private val filterTransform: (F) -> List<AbiValue<*>?>,
) {

    private lateinit var value: EventLog<In, F>

    operator fun getValue(any: Any, property: KProperty<*>): EventLog<In, F> {
        return if (::value.isInitialized) {
            value
        } else {
            val inputs = AbiTupleDsl().apply(params).params
            value = EventLog(property.name, inputs, paramsTransform, filterTransform)
            value
        }
    }
}

@AbiDsl
internal fun <T : Any, F : Any> eventLog(
    @AbiDsl params: AbiTupleDsl.() -> Unit,
    paramsTransform: (List<AbiValue<*>>) -> T,
    filterTransform: (F) -> List<AbiValue<*>?>,
): EventLogDelegate<T, F> = EventLogDelegate(params, paramsTransform, filterTransform)
