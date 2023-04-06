package com.github.nailkhaf.feature.transfers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.consume
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class TransferListComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        TransferListModel(
            get(),
            savedState = stateKeeper.consume(TransferListModel.key)
        )
    }

    init {
//        lifecycle.doOnStart { model.detectTransfers() }
//        lifecycle.doOnStop { model.stopDetectingTransfers() }
        stateKeeper.register(TransferListModel.key) { model.state.value }
    }
}

