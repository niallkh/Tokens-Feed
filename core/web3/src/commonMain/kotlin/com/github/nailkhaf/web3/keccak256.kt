package com.github.nailkhaf.web3

import com.github.nailkhaf.web3.models.Bytes32
import okio.ByteString

expect fun keccak256(byteString: ByteString): Bytes32
