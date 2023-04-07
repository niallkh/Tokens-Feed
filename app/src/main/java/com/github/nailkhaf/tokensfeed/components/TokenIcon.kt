@file:OptIn(ExperimentalAnimationApi::class)

package com.github.nailkhaf.tokensfeed.components

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    AnimatedContent(targetState = icon, transitionSpec = { fadeIn() with fadeOut() }) {
        if (it == null) {
            TextIcon(text = tokenSymbol)
        } else {
            Image(modifier = Modifier.size(40.dp), bitmap = it, contentDescription = null)
        }
    }
}

@Composable
fun loadAssetIcon(name: String): State<ImageBitmap?> {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            runCatching {
                context.assets.open("icons/$name.webp").buffered().use {
                    BitmapFactory.decodeStream(it).asImageBitmap()
                }
            }.getOrNull()
        }
    }
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
