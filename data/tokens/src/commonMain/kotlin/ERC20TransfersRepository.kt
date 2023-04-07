package com.github.nailkhaf.data.tokens

import com.github.nailkhaf.data.tokens.models.Erc20TokenTransfer
import com.github.nailkhaf.web3.models.Address
import kotlinx.coroutines.flow.Flow

interface ERC20TransfersRepository {

    fun getTransfers(
        chainId: ULong = 1uL,
        account: Address,
        limit: UInt = 50u
    ): Flow<List<Erc20TokenTransfer>>

    suspend fun detectNewIncomingERC20Transfers(
        chainId: ULong = 1uL,
        account: Address,
        limit: UInt = 50u
    )
}