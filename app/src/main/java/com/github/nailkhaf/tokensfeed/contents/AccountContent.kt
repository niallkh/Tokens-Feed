@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.github.nailkhaf.tokensfeed.contents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.nailkhaf.feature.account.AccountComponent
import com.github.nailkhaf.tokensfeed.R
import kotlinx.coroutines.launch

@Composable
fun LazyItemScope.AccountContent(
    accountComponent: AccountComponent,
    modifier: Modifier = Modifier
) {
    val state by accountComponent.model.state.collectAsState()

    AddressTextField(
        modifier = modifier
            .padding(start = 16.dp, end = 8.dp)
            .fillMaxWidth()
            .animateItemPlacement(),
        account = state.account,
        error = state.error,
        submitted = state.submitted,
        onSubmit = accountComponent.model::onSubmit,
        onFocussed = accountComponent.model::onFocussed
    )
}

@Composable
fun AddressTextField(
    account: String,
    error: String?,
    submitted: Boolean,
    onFocussed: () -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(account) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .onFocusChanged { if (it.isFocused) onFocussed() },
        singleLine = true,
        label = {
            if (error == null) {
                Text(stringResource(id = R.string.account_input_label))
            } else {
                Text(text = error)
            }
        },
        value = text,
        onValueChange = { text = it },
        keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onSubmit(text)
        }),
        isError = error != null,
    )

    LaunchedEffect(submitted) {
        if (submitted) {
            launch {
                focusManager.clearFocus()
            }
        }
    }
}
