@file:OptIn(ExperimentalFoundationApi::class)

package com.github.nailkhaf.tokensfeed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
    AppScaffold(modifier = modifier) {

        val balances = BalancesContent(balanceListComponent = mainComponent.balancesComponent)
        val transfers = TransfersContent(transferListComponent = mainComponent.transfersComponent)

        LazyColumn(
            modifier = it,
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item("account") {
                AccountContent(
                    accountComponent = mainComponent.accountComponent,
                )
            }
            item("section.balances") {
                SectionDivider(
                    stringResource(id = R.string.app_section_account_balances),
                )
            }
            balances()
            item("section.transfers") {
                SectionDivider(
                    stringResource(id = R.string.app_section_account_transfers),
                )
            }
            transfers()
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
        style = MaterialTheme.typography.overline,
        modifier = modifier
            .padding(start = 16.dp, top = 8.dp)
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
            TopAppBar(
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