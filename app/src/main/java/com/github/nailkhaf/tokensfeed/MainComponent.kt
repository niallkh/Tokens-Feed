package com.github.nailkhaf.tokensfeed

import com.arkivanov.decompose.ComponentContext
import com.github.nailkhaf.feature.account.AccountComponent
import com.github.nailkhaf.feature.balances.BalanceListComponent
import com.github.nailkhaf.feature.transfers.TransferListComponent

class MainComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    val accountComponent = AccountComponent(
        onAccountChanged = {
            balancesComponent.model.changeAccount(it)
            transfersComponent.model.changeAccount(it)
        },
        componentContext = this
    )

    val balancesComponent = BalanceListComponent(this)

    val transfersComponent = TransferListComponent(this)
}