package com.github.nailkhaf.feature.account

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.consume
import org.koin.core.component.KoinComponent

class AccountComponent(
    private val onAccountChanged: (String) -> Unit,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        AccountModel(
            onAccountChanged = onAccountChanged,
            savedState = stateKeeper.consume(AccountModel.key)
        )
    }

    init {
        stateKeeper.register(AccountModel.key) { model.state.value }
    }
}
