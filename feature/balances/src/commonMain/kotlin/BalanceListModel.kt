package com.github.nailkhaf.feature.balances

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.nailkhaf.data.tokens.ERC20TokensRepository
import com.github.nailkhaf.data.tokens.tokenlist.TokenList
import com.github.nailkhaf.web3.models.decodeAddress
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class BalanceListModel(
    private val tokensRepository: ERC20TokensRepository,
    savedState: State? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : InstanceKeeper.Instance,
    CoroutineScope by coroutineScope {

    private var detectTokensJob: Job? = null

    internal val state = MutableStateFlow(savedState ?: State())

    val balances: StateFlow<List<Balance>> = state
        .flatMapLatest {
            tokensRepository.getTokenBalances(
                it.chainId,
                it.account.decodeAddress()
            )
        }
        .map { it.map(::map) }
        .stateIn(this, SharingStarted.WhileSubscribed(), emptyList())

    fun changeAccount(account: String) {
        launch {
            state.update {
                it.copy(account = account)
            }
        }
        detectTokens()
    }

    fun detectTokens() {
        detectTokensJob?.cancel()
        detectTokensJob = launch {
            val state = state.value
            tokensRepository.detectNewERC20Tokens(
                chainId = state.chainId,
                account = state.account.decodeAddress(),
                tokenList = TokenList.OneInch,
            )
        }
    }

    fun stopDetectingTokens() {
        detectTokensJob?.cancel()
    }

    @Parcelize
    data class State(
        val chainId: ULong = 1u,
        val account: String = "0xcf4B8167378be0503f5674494188a89a1F401D44",
    ) : Parcelable

    override fun onDestroy() = cancel()

    companion object {
        val key = "BalanceListModel.State"
    }
}
