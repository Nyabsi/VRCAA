/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.components.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import kotlinx.coroutines.launch

@Composable
fun EmojiUploadConfigDialog(
    onDismiss: () -> Unit,
    onConfirmation: (type: String) -> Unit
) {
    val type = remember { mutableStateOf("aura") }
    val options = listOf(
        "aura", "bats", "bees", "bounce", "cloud", "confetti", "crying",
        "dislike", "fire", "idea", "lasers", "like", "magnet", "mistletoe",
        "money", "noise", "orbit", "pizza", "rain", "rotate", "shake",
        "snow", "snowball", "spin", "splash", "stop", "zzz"
    )
    val optionsFormat = mapOf(
        "aura" to "Aura", "bats" to "Bats", "bees" to "Bees", "bounce" to "Bounce", "cloud" to "Cloud", "confetti" to "Confetti", "crying" to "Crying",
        "dislike" to "Dislike", "fire" to "Fire", "idea" to "Idea", "lasers" to "Lasers", "like" to "Like", "magnet" to "Magnet", "mistletoe" to "Mistletoe",
        "money" to "Money", "noise" to "Noise", "orbit" to "Orbit", "pizza" to "Pizza", "rain" to "Rain", "rotate" to "Rotate", "shake" to "Shake",
        "snow" to "Snow", "snowball" to "Snowball", "spin" to "Spin", "splash" to "Splash", "stop" to "Stop", "zzz" to "ZZZ"
    )

    AlertDialog(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),

        title = {
            Text(text = "Emoji Upload Config")
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(8.dp)
            ) {
                item {
                    Text(
                        text = "Emoji Animation Type",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    ComboInput(options = options, selection = type, optionsFormat)
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(type.value)
                }
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}