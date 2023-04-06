package com.github.nailkhaf.feature.transfers

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.nailkhaf.data.tokens.ERC20TransfersRepository
import com.github.nailkhaf.web3.models.decodeAddress
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TransferListModel(
    private val tokensRepository: ERC20TransfersRepository,
    savedState: State? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : InstanceKeeper.Instance,
    CoroutineScope by coroutineScope {

    private var detectTransfersJob: Job? = null

    internal val state = MutableStateFlow(savedState ?: State())

    val transfers: StateFlow<List<Transfer>> = state
        .flatMapLatest {
            tokensRepository.getTransfers(
                chainId = it.chainId,
                account = it.account.decodeAddress(),
                limit = 50u
            )
        }
        .map { it.map(::map) }
        .stateIn(this, SharingStarted.WhileSubscribed(), emptyList())

    fun changeAccount(account: String) {
        launch {
            state.update { it.copy(account = account) }
        }
        detectTransfers()
    }

    fun detectTransfers() {
        detectTransfersJob?.cancel()
        detectTransfersJob = launch {
            val state = state.value
            tokensRepository.detectNewIncomingERC20Transfers(
                chainId = state.chainId,
                account = state.account.decodeAddress(),
                limit = 50u
            )
        }
    }

    fun stopDetectingTransfers() {
        detectTransfersJob?.cancel()
    }

    @Parcelize
    data class State(
        val chainId: ULong = 1u,
        val account: String = "0xcf4B8167378be0503f5674494188a89a1F401D44",
    ) : Parcelable

    override fun onDestroy() = cancel()

    companion object {
        val key = "TransfersListModel.key"
    }
}
