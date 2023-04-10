package com.github.nailkhaf.tokensfeed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun Erc20TokenIcon(
    tokenAddress: String,
    tokenSymbol: String
) {
    TokenIcon(key = tokenAddress.lowercase(), tokenSymbol = tokenSymbol)
}

@Composable
fun NativeTokenIcon(
    tokenSymbol: String
) {
    TokenIcon(key = tokenSymbol.lowercase(), tokenSymbol = tokenSymbol)
}

@Composable
private fun TokenIcon(key: String, tokenSymbol: String) {
    val icon by loadAssetIcon(name = key)

    icon?.let {
        Image(modifier = Modifier.size(40.dp), bitmap = it, contentDescription = null)
    }
        ?: TextIcon(text = tokenSymbol)
}

@Composable
fun TextIcon(
    text: String
) {
    Surface(
        Modifier
            .size(40.dp)
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = text,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Center
            )
        }
    }
}
