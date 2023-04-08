package com.github.nailkhaf.feature.account

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.CoroutineExceptionHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AccountComponent(
    componentContext: ComponentContext,
    coroutineExceptionHandler: CoroutineExceptionHandler,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        AccountModel(
            accountRepository = get(),
            addressValidator = get(),
            coroutineExceptionHandler = coroutineExceptionHandler
        )
    }
}
