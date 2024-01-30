package cc.sovellus.vrcaa.ui.screen.feed

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import cc.sovellus.vrcaa.ui.screen.feed.components.FeedItem
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.time.format.DateTimeFormatter

class FeedScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator: Navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { FeedScreenModel() }
        val feed = model.feed.collectAsState()

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(1.dp),
            state = rememberLazyListState()
        ) {
            items(
                feed.value.count(),
                key = { item -> feed.value.reversed()[item].feedId })
            {
                val item = feed.value.reversed()[it]
                when(item.type) {
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
                            resourceStringTitle = R.string.feed_online_label
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
                            resourceStringTitle = R.string.feed_offline_label
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
                            resourceStringTitle = R.string.feed_location_label
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
                            resourceStringTitle = R.string.feed_status_label
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
                            resourceStringTitle = R.string.feed_added_label
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
                            resourceStringTitle = R.string.feed_removed_label
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
                            resourceStringTitle = R.string.feed_friend_request_label
                        )
                    }
                    else -> { /* Unhandled */ }
                }
            }
        }
    }
}