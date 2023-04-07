@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.nailkhaf.tokensfeed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.nailkhaf.tokensfeed.contents.*

@Composable
fun MainContent(
    mainComponent: MainComponent,
    modifier: Modifier = Modifier
) {
    AppScaffold(modifier = modifier) { contentModifier ->

        Column(modifier = contentModifier.fillMaxHeight()) {
            AccountContent(
                accountComponent = mainComponent.accountComponent,
            )
            Spacer(modifier = Modifier.height(4.dp))

            val balances by mainComponent.balancesComponent.model.balances.collectAsState()
            val transfers by mainComponent.transfersComponent.model.transfers.collectAsState()

            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                item("section.balances") {
                    val loading by mainComponent.balancesComponent.model.loading.collectAsState()
                    SectionDivider(
                        stringResource(id = R.string.app_section_account_balances),
                        loading = loading
                    )
                }
                items(balances, key = { "balance:${it.id}" }) { balance ->
                    BalanceListItem(balance)
                }
                item("section.transfers") {
                    val loading by mainComponent.transfersComponent.model.loading.collectAsState()
                    SectionDivider(
                        stringResource(id = R.string.app_section_account_transfers),
                        loading = loading
                    )
                }
                items(transfers, key = { "transfer:${it.id}" }) { transfer ->
                    TransferListItem(transfer)
                }
            }
        }
    }
}

@Composable
private fun SectionDivider(
    title: String,
    loading: Boolean,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title)
                if (loading) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        },
        supportingContent = {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        },
    )
}

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
            )
        },
        content = {
            content(Modifier.padding(it))
        }
    )
}