package com.github.nailkhaf.feature.balances

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class BalanceListComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        BalanceListModel(get(), get())
    }

    init {
        lifecycle.doOnStart { model.detectTokens() }
        lifecycle.doOnStop { model.stopDetectingTokens() }
    }
}

