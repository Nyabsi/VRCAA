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

package cc.sovellus.vrcaa.ui.screen.feed

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.FeedManager
import cc.sovellus.vrcaa.ui.components.layout.FeedItem
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen

@Composable
fun FeedList(feed: SnapshotStateList<FeedManager.Feed>, filter: Boolean = false) {
    val navigator = LocalNavigator.currentOrThrow

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(1.dp),
        state = rememberLazyListState()
    ) {
        items(feed.reversed())
        { item ->
            when (item.type) {
                FeedManager.FeedType.FRIEND_FEED_ONLINE -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_online_text))
                        }
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_online_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(UserProfileScreen(item.friendId))
                            } else {
                                navigator.parent?.parent?.push(UserProfileScreen(item.friendId))
                            }
                        }
                    )
                }

                FeedManager.FeedType.FRIEND_FEED_OFFLINE -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_offline_text))
                        }
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_offline_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(UserProfileScreen(item.friendId))
                            } else {
                                navigator.parent?.parent?.push(UserProfileScreen(item.friendId))
                            }
                        }
                    )
                }

                FeedManager.FeedType.FRIEND_FEED_LOCATION -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_location_text))
                        }
                        append(" ")
                        append(item.travelDestination)
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_location_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(WorldScreen(item.worldId))
                            } else {
                                navigator.parent?.parent?.push(WorldScreen(item.worldId))
                            }
                        }
                    )
                }

                FeedManager.FeedType.FRIEND_FEED_STATUS -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_status_text))
                        }
                        append(" ")
                        append(item.friendStatus.toString())
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_status_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(UserProfileScreen(item.friendId))
                            } else {
                                navigator.parent?.parent?.push(UserProfileScreen(item.friendId))
                            }
                        }
                    )
                }

                FeedManager.FeedType.FRIEND_FEED_ADDED -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_added_text))
                        }
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_added_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(UserProfileScreen(item.friendId))
                            } else {
                                navigator.parent?.parent?.push(UserProfileScreen(item.friendId))
                            }
                        }
                    )
                }

                FeedManager.FeedType.FRIEND_FEED_REMOVED -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_removed_text))
                        }
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_removed_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(UserProfileScreen(item.friendId))
                            } else {
                                navigator.parent?.parent?.push(UserProfileScreen(item.friendId))
                            }
                        }
                    )
                }

                FeedManager.FeedType.FRIEND_FEED_FRIEND_REQUEST -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_friend_request_text))
                        }
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_friend_request_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(UserProfileScreen(item.friendId))
                            } else {
                                navigator.parent?.parent?.push(UserProfileScreen(item.friendId))
                            }
                        }
                    )
                }

                FeedManager.FeedType.FRIEND_FEED_AVATAR -> {
                    val text = buildAnnotatedString {
                        append(item.friendName)
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(stringResource(R.string.feed_friend_avatar_text))
                        }
                        append(" ")
                        append(item.avatarName)
                    }
                    FeedItem(
                        text = text,
                        friendPictureUrl = item.friendPictureUrl,
                        feedTimestamp = item.feedTimestamp,
                        resourceStringTitle = R.string.feed_friend_avatar_label,
                        onClick = {
                            if (filter) {
                                navigator.parent?.push(UserProfileScreen(item.friendId))
                            } else {
                                navigator.parent?.parent?.push(UserProfileScreen(item.friendId))
                            }
                        }
                    )
                }
            }
        }
    }
}

class FeedScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { FeedScreenModel() }

        val state by model.state.collectAsState()

        when (state) {
            is FeedScreenModel.FeedState.Loading -> LoadingIndicatorScreen().Content()
            is FeedScreenModel.FeedState.Result -> ShowScreen(model)
            else -> {}
        }
    }

    @Composable
    fun ShowScreen(model: FeedScreenModel) {
        val feed = model.feed.collectAsState()
        FeedList(feed.value)
    }
}
