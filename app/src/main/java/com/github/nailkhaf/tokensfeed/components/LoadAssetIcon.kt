@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.nailkhaf.tokensfeed.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.util.*

private val cache = WeakHashMap<String, ImageBitmap>()
private val dispatcher = Dispatchers.IO.limitedParallelism(1)

@Composable
fun loadAssetIcon(name: String): State<ImageBitmap?> {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null) {
        value = withContext(dispatcher) {

            val cached = cache[name]
            if (cached != null) {
                return@withContext cached
            }

            val loaded = runCatching {
                context.assets.open("icons/$name.webp").buffered().use {
                    BitmapFactory.decodeStream(it).asImageBitmap()
                }
            }.getOrNull()

            if (loaded != null) {
                cache[name] = loaded
            }

            loaded
        }
    }
}