@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.github.nailkhaf.tokensfeed.contents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.nailkhaf.feature.transfers.Transfer
import com.github.nailkhaf.tokensfeed.components.Erc20TokenIcon
import com.github.nailkhaf.tokensfeed.components.NativeTokenIcon

fun LazyListScope.TransfersContent(
    transfers: List<Transfer>
) {
    items(transfers, key = { "transfer:${it.id}" }) { transfer ->

        ListItem(
            modifier = Modifier.animateItemPlacement(),
            headlineContent = {
                Text(text = transfer.tokenName)
            },
            leadingContent = {
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
            trailingContent = {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = transfer.value)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = transfer.tokenSymbol)
                }
            },
            overlineContent = {
                Text(text = transfer.date)
            },
        )
    }
}
