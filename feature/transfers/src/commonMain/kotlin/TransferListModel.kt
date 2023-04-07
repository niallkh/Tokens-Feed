@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.nailkhaf.feature.transfers

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.github.nailkhaf.data.account.AccountRepository
import com.github.nailkhaf.data.tokens.ERC20TransfersRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TransferListModel(
    private val tokensRepository: ERC20TransfersRepository,
    private val accountRepository: AccountRepository,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : InstanceKeeper.Instance,
    CoroutineScope by coroutineScope {

    private var detectTransfersJob: Job? = null

    private val detecting = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = detecting

    val transfers: StateFlow<List<Transfer>> = accountRepository.account
        .flatMapLatest { account ->
            tokensRepository.getTransfers(account = account, limit = 50u)
        }
        .map { it.map(::map) }
        .flowOn(Dispatchers.Default)
        .stateIn(this, SharingStarted.WhileSubscribed(), emptyList())

    fun detectTransfers() {
        detectTransfersJob?.cancel()
        detectTransfersJob = launch {
            accountRepository.account.collectLatest { account ->
                detecting.value = true
                tokensRepository.detectNewIncomingERC20Transfers(
                    account = account,
                    limit = 50u
                )
                detecting.value = false
            }
        }
    }

    fun stopDetectingTransfers() {
        detectTransfersJob?.cancel()
        detecting.value = false
    }

    override fun onDestroy() = cancel()
}
