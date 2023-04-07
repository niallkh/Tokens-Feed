@file:OptIn(ExperimentalFoundationApi::class)

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
import com.github.nailkhaf.feature.balances.Balance
import com.github.nailkhaf.tokensfeed.components.Erc20TokenIcon
import com.github.nailkhaf.tokensfeed.components.NativeTokenIcon

fun LazyListScope.BalancesContent(
    balances: List<Balance>,
) {
    items(balances, key = { "balance:${it.id}" }) { balance ->

        ListItem(
            modifier = Modifier.animateItemPlacement(),
            headlineContent = {
                Text(text = balance.tokenName)
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
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = balance.balance)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = balance.tokenSymbol)
                }
            },
            supportingContent = balance.tokenAddress?.let {
                { Text(text = "${it.substring(0, 6)}...${it.substring(38, 42)}") }
            },
        )
    }
}
