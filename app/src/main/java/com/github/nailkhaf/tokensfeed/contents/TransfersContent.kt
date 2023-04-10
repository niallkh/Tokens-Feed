@file:OptIn(ExperimentalFoundationApi::class)

package com.github.nailkhaf.tokensfeed.contents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.nailkhaf.feature.transfers.Transfer
import com.github.nailkhaf.tokensfeed.components.Erc20TokenIcon
import com.github.nailkhaf.tokensfeed.components.NativeTokenIcon
import com.github.nailkhaf.tokensfeed.components.TokenValue

@Composable
fun LazyItemScope.TransferListItem(transfer: Transfer) {
    ListItem(
        modifier = Modifier
            .clickable { }
            .animateItemPlacement(),
        headlineContent = {
            Text(
                text = transfer.tokenName,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        },
        leadingContent = {
            transfer.tokenAddress?.let {
                Erc20TokenIcon(
                    tokenAddress = it,
                    tokenSymbol = transfer.tokenSymbol,
                )
            }
                ?: NativeTokenIcon(
                    tokenSymbol = transfer.tokenSymbol
                )
        },
        trailingContent = {
            TokenValue(value = transfer.value, symbol = transfer.tokenSymbol)
        },
        overlineContent = {
            Text(text = transfer.date)
        },
    )
}
