package com.github.nailkhaf.web3

import com.github.nailkhaf.web3.models.Bytes32
import com.github.nailkhaf.web3.models.asBytes32
import okio.ByteString
import okio.blackholeSink
import okio.buffer
import okio.hashingSink
import org.bouncycastle.jcajce.provider.digest.Keccak

actual fun keccak256(byteString: ByteString): Bytes32 {
    val hashingSink = blackholeSink().hashingSink(Keccak.Digest256())
    val buffer = hashingSink.buffer()
    buffer.write(byteString)
    buffer.flush()
    return hashingSink.hash.asBytes32
}