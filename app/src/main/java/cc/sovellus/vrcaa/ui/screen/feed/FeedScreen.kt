package cc.sovellus.vrcaa.ui.screen.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
import cc.sovellus.vrcaa.manager.FeedManager
import cc.sovellus.vrcaa.ui.components.layout.FeedItem
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen

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
        val navigator = LocalNavigator.currentOrThrow
        val feed = model.feed.collectAsState()
        var searchQuery by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.feed_search_placeholder),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                val filteredFeed = feed.value.filter { feedItem ->
                    feedItem.friendName.contains(searchQuery, ignoreCase = true) || (feedItem.type == FeedManager.FeedType.FRIEND_FEED_LOCATION && feedItem.travelDestination.contains(
                        searchQuery,
                        ignoreCase = true
                    ))
                }

                items(
                    filteredFeed.count(),
                    key = { item -> filteredFeed.reversed()[item].feedId })
                {
                    val item = filteredFeed.reversed()[it]
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
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(item.friendId)) }
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
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(item.friendId)) }
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
                                onClick = { navigator.parent?.parent?.push(WorldInfoScreen(item.worldId)) }
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
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(item.friendId)) }
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
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(item.friendId)) }
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
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(item.friendId)) }
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
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(item.friendId)) }
                            )
                        }
                    }
                }
            }
        }
    }
}