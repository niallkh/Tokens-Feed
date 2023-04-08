package com.github.nailkhaf.tokensfeed

import com.arkivanov.decompose.ComponentContext
import com.github.nailkhaf.feature.account.AccountComponent
import com.github.nailkhaf.feature.balances.BalanceListComponent
import com.github.nailkhaf.feature.transfers.TransferListComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow

class MainComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        error.value = null
        error.value = throwable.message
    }

    val error = MutableStateFlow<String?>(null)

    val accountComponent = AccountComponent(this, coroutineExceptionHandler)
    val balancesComponent = BalanceListComponent(this, coroutineExceptionHandler)
    val transfersComponent = TransferListComponent(this, coroutineExceptionHandler)
}