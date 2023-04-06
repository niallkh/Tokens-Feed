@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.github.nailkhaf.tokensfeed.contents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.nailkhaf.feature.transfers.TransferListComponent
import com.github.nailkhaf.tokensfeed.components.Erc20TokenIcon
import com.github.nailkhaf.tokensfeed.components.NativeTokenIcon

@Composable
fun TransfersContent(
    transferListComponent: TransferListComponent,
): LazyListScope.() -> Unit {
    val transfers by transferListComponent.model.transfers.collectAsState()

    return {
        items(transfers, key = { "transfer:${it.id}" }) { transfer ->

            ListItem(
                modifier = Modifier.animateItemPlacement(),
                text = {
                    Text(text = transfer.tokenName)
                },
                icon = {
                    transfer.tokenAddress?.let {
                        Erc20TokenIcon(
                            tokenAddress = it,
                            tokenSymbol = transfer.tokenSymbol
                        )
                    }
                        ?: NativeTokenIcon(
                            tokenSymbol = transfer.tokenSymbol
                        )
                },
                trailing = {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = transfer.value)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = transfer.tokenSymbol)
                    }
                },
                overlineText = {
                    Text(text = transfer.date)
                },
            )
        }
    }
}
