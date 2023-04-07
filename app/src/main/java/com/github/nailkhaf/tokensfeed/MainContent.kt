@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.nailkhaf.tokensfeed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.nailkhaf.tokensfeed.contents.AccountContent
import com.github.nailkhaf.tokensfeed.contents.BalancesContent
import com.github.nailkhaf.tokensfeed.contents.TransfersContent

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
                    SectionDivider(
                        stringResource(id = R.string.app_section_account_balances),
                    )
                }
                BalancesContent(balances)
                item("section.transfers") {
                    SectionDivider(
                        stringResource(id = R.string.app_section_account_transfers),
                    )
                }
                TransfersContent(transfers)
            }
        }
    }
}

@Composable
private fun SectionDivider(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(start = 16.dp, top = 8.dp)
    )
    Divider(modifier = modifier)
    Spacer(modifier = modifier.height(8.dp))
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