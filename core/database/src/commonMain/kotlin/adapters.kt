package com.github.nailkhaf.database

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import com.ionspin.kotlin.bignum.integer.util.toTwosComplementByteArray
import com.squareup.sqldelight.ColumnAdapter
import okio.ByteString
import okio.ByteString.Companion.toByteString

internal object BigIntegerAdapter : ColumnAdapter<BigInteger, ByteArray> {

    override fun decode(databaseValue: ByteArray): BigInteger {
        return if (databaseValue.isEmpty()) {
            BigInteger.ZERO
        } else {
            BigInteger.fromTwosComplementByteArray(databaseValue)
        }
    }

    override fun encode(value: BigInteger): ByteArray {
        return if (value == BigInteger.ZERO) {
            byteArrayOf()
        } else {
            value.toTwosComplementByteArray()
        }
    }
}

internal object ByteStringAdapter : ColumnAdapter<ByteString, ByteArray> {
    override fun decode(databaseValue: ByteArray): ByteString {
        return databaseValue.toByteString()
    }

    override fun encode(value: ByteString): ByteArray {
        return value.toByteArray()
    }
}
