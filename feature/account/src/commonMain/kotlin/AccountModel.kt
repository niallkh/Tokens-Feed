package com.github.nailkhaf.feature.account

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.nailkhaf.web3.models.decodeAddress
import com.github.nailkhaf.web3.models.formatChecksum
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class AccountModel(
    private val onAccountChanged: (String) -> Unit,
    savedState: State? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : InstanceKeeper.Instance,
    CoroutineScope by coroutineScope {

    val state = MutableStateFlow(savedState ?: State())

    fun onFocussed() {
        launch {
            state.update { it.copy(submitted = false) }
        }
    }

    fun onSubmit(text: String) {
        launch {
            val error = validateAddress(text)
            if (error == null) {
                state.update { it.copy(error = null, account = text, submitted = true) }
                onAccountChanged(text.decodeAddress().formatChecksum())
            } else {
                state.update { it.copy(error = error, submitted = false) }
            }
        }
    }

    @Parcelize
    data class State(
        val account: String = "0xcf4B8167378be0503f5674494188a89a1F401D44",
        val error: String? = null,
        val submitted: Boolean = true,
    ) : Parcelable

    override fun onDestroy() = cancel()

    companion object {
        val key = "AccountModel.key"
    }

}

private fun validateAddress(text: String): String? {
    val address by lazy(LazyThreadSafetyMode.NONE) { runCatching { text.decodeAddress() } }
    return when {
        text.startsWith("0x", ignoreCase = true).not() -> "Address must have prefix 0x"
        text.length != 42 -> "Address must have 42 symbols"
        text.matches(Regex("^0[xX][0-9a-fA-F]{40}$")).not() -> "Address must be in hex format"
        address.isFailure -> "Address is invalid"
        text.contains(Regex("[A-F]"))
                && address.getOrThrow().formatChecksum() != text -> "Wrong checksum"
        else -> null
    }
}
