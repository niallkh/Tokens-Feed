package com.github.nailkhaf.data.account

import com.github.nailkhaf.web3.models.Address
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val account: Flow<Address>
    suspend fun changeAccount(account: Address)
}