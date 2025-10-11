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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.INotifications
import cc.sovellus.vrcaa.api.vrchat.http.models.NotificationV2
import cc.sovellus.vrcaa.helper.JsonHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.NotificationManager
import kotlinx.coroutines.launch

@Composable
fun NotificationDialogV2(notification: NotificationV2, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.notification_dialog_title), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = notification.message, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(stringResource(R.string.generic_text_cancel))
                    }
                    for (response in notification.responses) {
                        when (response.type) {
                            "accept" -> {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        api.notifications.respondToNotification(notification.id, INotifications.ResponseType.ACCEPT, response.data)
                                        NotificationManager.removeNotificationV2(notification.id)
                                        onDismiss()
                                    }
                                }) {
                                    Text(stringResource(R.string.notification_dialog_button_accept))
                                }
                            }

                            "reject" -> {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        api.notifications.respondToNotification(notification.id, INotifications.ResponseType.REJECT, response.data)
                                        NotificationManager.removeNotificationV2(notification.id)
                                        onDismiss()
                                    }
                                }) {
                                    Text(stringResource(R.string.notification_dialog_button_reject))
                                }
                            }

                            "block" -> {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        api.notifications.respondToNotification(notification.id, INotifications.ResponseType.BLOCK, response.data)
                                        NotificationManager.removeNotificationV2(notification.id)
                                        onDismiss()
                                    }
                                }) {
                                    Text(stringResource(R.string.notification_dialog_button_block))
                                }
                            }

                            "delete" -> {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        api.notifications.respondToNotification(notification.id, INotifications.ResponseType.DELETE, response.data)
                                        NotificationManager.removeNotificationV2(notification.id)
                                        onDismiss()
                                    }
                                }) {
                                    Text(stringResource(R.string.notification_dialog_button_delete))
                                }
                            }

                            "unsubscribe" -> {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        api.notifications.respondToNotification(notification.id, INotifications.ResponseType.UNSUBSCRIBE, response.data)
                                        NotificationManager.removeNotificationV2(notification.id)
                                        onDismiss()
                                    }
                                }) {
                                    Text(stringResource(R.string.notification_dialog_button_unsubscribe))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}