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
import com.github.nailkhaf.feature.balances.BalanceListComponent
import com.github.nailkhaf.tokensfeed.components.Erc20TokenIcon
import com.github.nailkhaf.tokensfeed.components.NativeTokenIcon

@Composable
fun BalancesContent(
    balanceListComponent: BalanceListComponent,
): LazyListScope.() -> Unit {
    val balances by balanceListComponent.model.balances.collectAsState()

    return {
        items(balances, key = { "balance:${it.id}" }) { balance ->

            ListItem(
                modifier = Modifier.animateItemPlacement(),
                text = {
                    Text(text = balance.tokenName)
                },
                icon = {
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
                trailing = {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = balance.balance)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = balance.tokenSymbol)
                    }
                },
                secondaryText = balance.tokenAddress?.let {
                    { Text(text = "${it.substring(0, 6)}...${it.substring(38, 42)}") }
                },
                singleLineSecondaryText = true
            )
        }
    }
}
