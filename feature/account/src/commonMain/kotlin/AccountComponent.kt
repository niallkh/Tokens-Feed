package com.github.nailkhaf.feature.account

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AccountComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        AccountModel(
            accountRepository = get()
        )
    }
}
