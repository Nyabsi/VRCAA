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

package cc.sovellus.vrcaa.ui.screen.presence

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.dialog.InputDialog

class RichPresenceScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val model = navigator.rememberNavigatorScreenModel { RichPresenceScreenModel() }
        val dialogState = remember { mutableStateOf(false) }

        if (dialogState.value) {
            InputDialog(
                onDismiss = {
                    dialogState.value = false
                },
                onConfirmation = {
                    dialogState.value = false
                    model.setWebSocket()
                },
                title = "Set Webhook Url",
                text = model.websocket
            )
        }

        LaunchedEffect(Unit) {
            model.updateTokenIfChanged()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },

                    title = { Text(text = "Rich Presence") }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "Account",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = { Text(if (model.token.value.isNotEmpty()) { "Connected" } else { "Disconnected" }) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null
                                )
                            },
                            trailingContent = {
                                if (model.token.value.isNotEmpty()) {
                                    Button(onClick = { model.revoke() }) {
                                        Text(text = "Disconnect Discord")
                                    }
                                } else {
                                    Button(onClick = { navigator.push(RichPresenceWebViewLogin()) }) {
                                        Text(text = "Connect Discord")
                                    }
                                }
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "Settings",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }

                    item {

                        ListItem(
                            headlineContent = { Text(stringResource(R.string.discord_login_label_enable)) },
                            supportingContent = { Text(stringResource(R.string.discord_login_description_enable)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = null
                                )
                            },
                            trailingContent = {
                                Switch(
                                    enabled = model.token.value.isNotEmpty(),
                                    checked = model.enabled.value,
                                    onCheckedChange = {
                                        model.toggle()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                    ),
                                )
                            },
                            modifier = Modifier.clickable(
                                enabled = model.enabled.value,
                                onClick = {
                                    model.toggle()
                                }
                            )
                        )
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.discord_login_label_set_webhook)) },
                            supportingContent = { Text(stringResource(R.string.discord_login_description_set_webhook)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ImageSearch,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable(
                                enabled = model.enabled.value,
                                onClick = {
                                    if (model.token.value.isNotEmpty())
                                        dialogState.value = true
                                }
                            )
                        )
                    }
                }
            }
        )
    }
}