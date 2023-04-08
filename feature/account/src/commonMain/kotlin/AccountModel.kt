package com.github.nailkhaf.feature.account

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.nailkhaf.data.account.AccountRepository
import com.github.nailkhaf.web3.models.formatChecksum
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AccountModel(
    private val accountRepository: AccountRepository,
    private val addressValidator: AddressValidator,
    coroutineExceptionHandler: CoroutineExceptionHandler,
) : InstanceKeeper.Instance,
    CoroutineScope by CoroutineScope(coroutineExceptionHandler + Dispatchers.Main + SupervisorJob()) {

    val state = MutableStateFlow(State())

    val account: StateFlow<String> = accountRepository.account
        .map { it.formatChecksum() }
        .flowOn(Dispatchers.Default)
        .stateIn(this, SharingStarted.WhileSubscribed(), "")

    fun onSelect() {
        launch {
            state.update { it.copy(submitted = false) }
        }
    }

    fun onSubmit(text: String) {
        launch {
            val result: AddressValidator.Result = addressValidator(text)

            if (result is AddressValidator.Result.Success) {
                state.update { it.copy(error = null, submitted = true) }
                accountRepository.changeAccount(result.address)
            } else {
                val error = when (result) {
                    AddressValidator.Result.WrongFormat -> "Address in wrong format"
                    AddressValidator.Result.WrongChecksum -> "Address has wrong checksum"
                    is AddressValidator.Result.Success -> error("Smth went wrong")
                }
                state.update { it.copy(error = error, submitted = false) }
            }
        }
    }

    @Parcelize
    data class State(
        val error: String? = null,
        val submitted: Boolean = false,
    ) : Parcelable

    override fun onDestroy() = cancel()
}
