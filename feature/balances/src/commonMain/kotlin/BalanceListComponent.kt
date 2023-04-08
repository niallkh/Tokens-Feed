package com.github.nailkhaf.feature.balances

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import kotlinx.coroutines.CoroutineExceptionHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class BalanceListComponent(
    componentContext: ComponentContext,
    coroutineExceptionHandler: CoroutineExceptionHandler,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        BalanceListModel(
            tokensRepository = get(),
            accountRepository = get(),
            coroutineExceptionHandler = coroutineExceptionHandler
        )
    }

    init {
        lifecycle.doOnStart { model.detectTokens() }
        lifecycle.doOnStop { model.stopDetectingTokens() }
    }
}

