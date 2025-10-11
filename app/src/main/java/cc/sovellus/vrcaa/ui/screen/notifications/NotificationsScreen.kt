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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.JsonHelper
import cc.sovellus.vrcaa.ui.components.dialog.NotificationDialog
import cc.sovellus.vrcaa.ui.components.dialog.NotificationDialogV2
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun NotificationItem(title: String, message: AnnotatedString, url: String, date: String, onClick: () -> Unit) {
    ListItem(
        overlineContent = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        headlineContent = {
            Text(
                text = message,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            if (url.isNotEmpty()) {
                Column {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        GlideImage(
                            model = url,
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
            } else {
                Box(modifier = Modifier.size(64.dp))
            }
        },
        trailingContent = {
            val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
            Text(text = ZonedDateTime.parse(date).format(formatter))
        },
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

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
        var showDialogV2 by remember { mutableStateOf(false) }

        if (showDialog) {
            model.currentNotification.value?.let {
                NotificationDialog(
                    it,
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }

        if (showDialogV2) {
            model.currentNotificationV2.value?.let {
                NotificationDialogV2(
                    it,
                    onDismiss = {
                        showDialogV2 = false
                    }
                )
            }
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
                    val notificationsV2 = model.notificationsV2.collectAsState()

                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(1.dp),
                        state = rememberLazyListState()
                    ) {
                        items(notificationsV2.value) { notificationV2 ->
                            val text = buildAnnotatedString {
                                append(notificationV2.message)
                            }
                            NotificationItem(
                                // if it's some strange notification, just show the type instead of title.
                                notificationV2.title ?: notificationV2.type,
                                text,
                                notificationV2.imageUrl ?: "", // an image url is not an guarantee.
                                notificationV2.createdAt
                            ) {
                                model.currentNotificationV2.value = notificationV2
                                showDialogV2 = true
                            }
                        }

                        items(notifications.value) { notification ->
                            val user = remember { model.users.find { it?.id == notification.senderUserId } }
                            when (notification.type) {
                                "friendRequest" -> {
                                    user?.let {
                                        val text = buildAnnotatedString {
                                            append(stringResource(R.string.notifications_type_friend_request_content).format(notification.senderUsername))
                                        }
                                        NotificationItem(
                                            stringResource(R.string.notifications_type_friend_request),
                                            text,
                                            user.userIcon.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } },
                                            notification.createdAt
                                        ) {
                                            model.currentNotification.value = notification
                                            showDialog = true
                                        }
                                    }
                                }
                                "message" -> {
                                    user?.let {
                                        val text = buildAnnotatedString {
                                            append(notification.message)
                                        }
                                        NotificationItem(
                                            user.displayName,
                                            text,
                                            user.userIcon.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } },
                                            notification.createdAt
                                        ) {
                                            model.currentNotification.value = notification
                                            showDialog = true
                                        }
                                    }
                                }
                                "invite" -> {
                                    user?.let {
                                        val text = buildAnnotatedString {
                                            if (notification.message.isNotEmpty()) {
                                                append(notification.message)
                                            } else {
                                                append(user.displayName)
                                                append(" ")
                                                withStyle(style = SpanStyle(color = Color.Gray)) {
                                                    append(stringResource(R.string.notification_invite_text))
                                                }
                                                append(" ")
                                                append(JsonHelper.getJsonField(notification.details, "worldName") ?: "Invalid Name")
                                                val message = JsonHelper.getJsonField(notification.details, "inviteMessage")
                                                message?.let {
                                                    append(":")
                                                    append(" ")
                                                    withStyle(style = SpanStyle(color = Color.Gray, fontStyle = FontStyle.Italic)) {
                                                        append("\"")
                                                        append(message)
                                                        append("\"")
                                                    }
                                                }
                                            }
                                        }

                                        NotificationItem(
                                            stringResource(R.string.notifications_type_invite),
                                            text,
                                            user.userIcon.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } },
                                            notification.createdAt
                                        ) {
                                            model.currentNotification.value = notification
                                            showDialog = true
                                        }
                                    }
                                }
                                "inviteResponse" -> {
                                    user?.let {
                                        val text = buildAnnotatedString {
                                            if (notification.message.isNotEmpty()) {
                                                append(notification.message)
                                            } else {
                                                append(user.displayName)
                                                append(" ")
                                                withStyle(style = SpanStyle(color = Color.Gray)) {
                                                    append(stringResource(R.string.notification_invite_response_text))
                                                }
                                                val message = JsonHelper.getJsonField(notification.details, "responseMessage")
                                                message?.let {
                                                    append(":")
                                                    append(" ")
                                                    withStyle(style = SpanStyle(color = Color.Gray, fontStyle = FontStyle.Italic)) {
                                                        append("\"")
                                                        append(message)
                                                        append("\"")
                                                    }
                                                }
                                            }
                                        }
                                        NotificationItem(
                                            stringResource(R.string.notifications_type_invite_response),
                                            text,
                                            user.userIcon.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } },
                                            notification.createdAt
                                        ) {
                                            model.currentNotification.value = notification
                                            showDialog = true
                                        }
                                    }
                                }
                                "requestInvite" -> {
                                    user?.let {
                                        val text = buildAnnotatedString {
                                            if (notification.message.isNotEmpty()) {
                                                append(notification.message)
                                            } else {
                                                append(user.displayName)
                                                append(" ")
                                                withStyle(style = SpanStyle(color = Color.Gray)) {
                                                    append(stringResource(R.string.notification_invite_request_text))
                                                }
                                                val message = JsonHelper.getJsonField(notification.details, "requestMessage")
                                                message?.let {
                                                    append(":")
                                                    append(" ")
                                                    withStyle(style = SpanStyle(color = Color.Gray, fontStyle = FontStyle.Italic)) {
                                                        append("\"")
                                                        append(message)
                                                        append("\"")
                                                    }
                                                }
                                            }
                                        }

                                        NotificationItem(
                                            stringResource(R.string.notifications_type_invite_request),
                                            text,
                                            user.userIcon.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } },
                                            notification.createdAt
                                        ) {
                                            model.currentNotification.value = notification
                                            showDialog = true
                                        }
                                    }
                                }
                                "requestInviteResponse" -> {
                                    user?.let {
                                        val text = buildAnnotatedString {
                                            if (notification.message.isNotEmpty()) {
                                                append(notification.message)
                                            } else {
                                                append(user.displayName)
                                                append(" ")
                                                withStyle(style = SpanStyle(color = Color.Gray)) {
                                                    append(stringResource(R.string.notification_invite_request_response_text))
                                                }
                                                val message = JsonHelper.getJsonField(notification.details, "responseMessage")
                                                message?.let {
                                                    append(":")
                                                    append(" ")
                                                    withStyle(style = SpanStyle(color = Color.Gray, fontStyle = FontStyle.Italic)) {
                                                        append("\"")
                                                        append(message)
                                                        append("\"")
                                                    }
                                                }
                                            }
                                        }
                                        NotificationItem(
                                            stringResource(R.string.notifications_type_invite_request_response),
                                            text,
                                            user.userIcon.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } },
                                            notification.createdAt
                                        ) {
                                            model.currentNotification.value = notification
                                            showDialog = true
                                        }
                                    }
                                }
                                else -> {
                                    user?.let {
                                        val text = buildAnnotatedString {
                                            append(notification.message.ifEmpty { "This notification doesn't have an message." })
                                        }
                                        NotificationItem(
                                            notification.type,
                                            text,
                                            user.userIcon.ifEmpty { user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl } },
                                            notification.createdAt
                                        ) {
                                            model.currentNotification.value = notification
                                            showDialog = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}