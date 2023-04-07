package com.github.nailkhaf.data.account

import com.github.nailkhaf.datastore.AppStore
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.asAddress
import com.github.nailkhaf.web3.models.decodeAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import okio.ByteString

private val DEFAULT_ADDRESS = "0xcf4B8167378be0503f5674494188a89a1F401D44".decodeAddress()

class DefaultAccountRepository(
    private val store: AppStore,
) : AccountRepository {

    override val account: Flow<Address>
        get() = store.data
            .map { it.account.takeIf { it != ByteString.EMPTY }?.asAddress ?: DEFAULT_ADDRESS }
            .onStart { emit(DEFAULT_ADDRESS) }

    override suspend fun changeAccount(account: Address) {
        store.updateData {
            it.copy(account = account.bytes)
        }
    }
}