package com.github.nailkhaf.web3.abi

import com.ionspin.kotlin.bignum.integer.toBigInteger
import okio.ByteString

object TestContract {

    val bar by functionCall<Pair<ByteString, ByteString>>(
        params = {
            array(size = 2u) {
                bytes(3u)
            }
        },
        paramsTransform = { (first, second) ->
            listOf(
                listOf(
                    first.abi,
                    second.abi
                ).array
            )
        },
    )

    val baz by functionCall<Pair<UInt, Boolean>, Boolean>(
        params = {
            uint32()
            bool()
        },
        paramsTransform = { (a, b) -> listOf(a.toBigInteger().abi, b.abi) },
        returns = { bool() },
        returnsTransform = ::castFirst
    )

    val sam by functionCall<Triple<ByteString, Boolean, List<UInt>>>(
        params = {
            bytes()
            bool()
            array {
                uint256()
            }
        },
        paramsTransform = { (a, b, c) ->
            listOf(
                a.abi,
                b.abi,
                c.map { it.toBigInteger().abi }.array
            )
        },
    )

    val f by functionCall<FParams>(
        params = {
            uint256()
            array {
                uint32()
            }
            bytes(bytes = 10u)
            bytes()
        },
        paramsTransform = { (a, b, c, d) ->
            listOf(
                a.toBigInteger().abi,
                b.map { it.toBigInteger().abi }.array,
                c.abi,
                d.abi
            )
        }
    )

    data class FParams(
        val a: ULong,
        val b: List<UInt>,
        val c: ByteString,
        val d: ByteString
    )

    val g by functionCall<Pair<List<List<ULong>>, List<String>>>(
        params = {
            array { array { uint256() } }
            array { string() }
        },
        paramsTransform = { (a, b) ->
            listOf(
                a.map { it.map { it.toBigInteger().abi }.array }.array,
                b.map { it.abi }.array
            )
        }
    )
}
