@file:OptIn(ExperimentalFoundationApi::class)

package com.github.nailkhaf.tokensfeed.contents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.nailkhaf.feature.balances.Balance
import com.github.nailkhaf.tokensfeed.components.Erc20TokenIcon
import com.github.nailkhaf.tokensfeed.components.NativeTokenIcon
import com.github.nailkhaf.tokensfeed.components.TokenValue

@Composable
fun LazyItemScope.BalanceListItem(balance: Balance) {
    ListItem(
        modifier = Modifier.animateItemPlacement(),
        headlineContent = {
            Text(
                text = balance.tokenName,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        leadingContent = {
            balance.tokenAddress?.let {
                Erc20TokenIcon(
                    tokenAddress = it,
                    tokenSymbol = balance.tokenSymbol
                )
            }
                ?: NativeTokenIcon(
                    tokenSymbol = balance.tokenSymbol
                )
        },
        trailingContent = {
            TokenValue(value = balance.balance, symbol = balance.tokenSymbol)
        },
        supportingContent = balance.tokenAddress?.let {
            { Text(text = "${it.substring(0, 6)}...${it.substring(38, 42)}") }
        },
    )
}
