package com.github.nailkhaf.feature.balances

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.essenty.statekeeper.consume
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class BalanceListComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        BalanceListModel(
            get(),
            savedState = stateKeeper.consume(BalanceListModel.key)
        )
    }

    init {
        lifecycle.doOnStart { model.detectTokens() }
        lifecycle.doOnStop { model.stopDetectingTokens() }
        stateKeeper.register(BalanceListModel.key) { model.state.value }
    }
}

