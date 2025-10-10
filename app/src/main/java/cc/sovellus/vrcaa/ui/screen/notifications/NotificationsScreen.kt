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

package cc.sovellus.vrcaa.ui.screen.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.NotificationManager
import cc.sovellus.vrcaa.ui.components.dialog.NotificationDialog
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.text.ifEmpty

class NotificationsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { NotificationsScreenModel() }

        val state by model.state.collectAsState()

        when (state) {
            is NotificationsScreenModel.NotificationsState.Loading -> LoadingIndicatorScreen().Content()
            is NotificationsScreenModel.NotificationsState.Loaded -> ShowNotifications(model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    @Composable
    fun ShowNotifications(model: NotificationsScreenModel) {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            NotificationDialog(
                model.currentNotificationId.value,
                onDismiss = {
                    showDialog = false
                }
            )
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

                    title = { Text(text = stringResource(R.string.notifications_page_title)) }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        )
                ) {
                    val notifications = model.notifications.collectAsState()

                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(1.dp),
                        state = rememberLazyListState()
                    ) {
                        items(notifications.value) { notification ->
                            when (notification.type) {
                                "friendRequest" -> {
                                    val user = remember { model.users.find { it?.id == notification.senderUserId } }
                                    ListItem(
                                        overlineContent = {
                                            Text(text = stringResource(R.string.notifications_type_friend_request))
                                        },
                                        headlineContent = {
                                            Text(stringResource(R.string.notifications_type_friend_request_content).format(notification.senderUsername))
                                        },
                                        leadingContent = {
                                            Column {
                                                Badge(
                                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .align(Alignment.CenterHorizontally)
                                                ) {
                                                    GlideImage(
                                                        model = user?.userIcon?.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } } ?: "",
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(56.dp)
                                                            .clip(RoundedCornerShape(50)),
                                                        contentScale = ContentScale.FillBounds,
                                                        alignment = Alignment.Center,
                                                        loading = placeholder(R.drawable.image_placeholder),
                                                        failure = placeholder(R.drawable.image_placeholder)
                                                    )
                                                }
                                            }
                                        },
                                        trailingContent = {
                                            val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                                                .withLocale(Locale.getDefault())
                                            Text(text = ZonedDateTime.parse(notification.createdAt).format(formatter))
                                        },
                                        modifier = Modifier.clickable {
                                            model.currentNotificationId.value = notification.id
                                            showDialog = true
                                        }
                                    )
                                }
                                else -> {
                                    ListItem(
                                        overlineContent = {
                                            Text(text = notification.type)
                                        },
                                        headlineContent = {
                                            Text("This type is not known!")
                                        },
                                        supportingContent = {
                                            Text("contact me @ discord and let me know.")
                                        },
                                        trailingContent = {
                                            val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                                                .withLocale(Locale.getDefault())
                                            Text(text = ZonedDateTime.parse(notification.createdAt).format(formatter))
                                        },
                                        modifier = Modifier.clickable {
                                            model.currentNotificationId.value = notification.id
                                            showDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}