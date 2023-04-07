package com.github.nailkhaf.feature.transfers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class TransferListComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    val model = instanceKeeper.getOrCreate {
        TransferListModel(get(), get())
    }

    init {
        lifecycle.doOnStart { model.detectTransfers() }
        lifecycle.doOnStop { model.stopDetectingTransfers() }
    }
}

