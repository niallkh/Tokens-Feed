@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.nailkhaf.feature.balances

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.github.nailkhaf.data.account.AccountRepository
import com.github.nailkhaf.data.tokens.ERC20TokensRepository
import com.github.nailkhaf.data.tokens.tokenlist.TokenList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class BalanceListModel(
    private val tokensRepository: ERC20TokensRepository,
    private val accountRepository: AccountRepository,
    coroutineExceptionHandler: CoroutineExceptionHandler,
) : InstanceKeeper.Instance,
    CoroutineScope by CoroutineScope(coroutineExceptionHandler + Dispatchers.Main + SupervisorJob()) {

    private var detectTokensJob: Job? = null

    private val detecting = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = detecting

    val balances: StateFlow<List<Balance>> = accountRepository.account
        .flatMapLatest { tokensRepository.getTokenBalances(account = it) }
        .map { it.map(::map) }
        .flowOn(Dispatchers.Default)
        .stateIn(this, SharingStarted.WhileSubscribed(), emptyList())

    fun detectTokens() {
        detectTokensJob?.cancel()
        detectTokensJob = launch {
            accountRepository.account.collectLatest { account ->
                detecting.value = true
                try {
                    tokensRepository.detectNewERC20Tokens(
                        account = account,
                        tokenList = TokenList.OneInch,
                    )
                } finally {
                    detecting.value = false
                }
            }
        }
    }

    fun stopDetectingTokens() {
        detectTokensJob?.cancel()
        detecting.value = false
    }

    override fun onDestroy() = cancel()
}
