package cc.sovellus.vrcaa.ui.screen.activities

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
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

class ActivitiesScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { ActivitiesScreenModel() }

        val options = stringArrayResource(R.array.activities_selection_options)
        val icons = listOf(Icons.Filled.RssFeed, Icons.Filled.Notifications, Icons.Filled.Groups)

        MultiChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    icon = {
                        SegmentedButtonDefaults.Icon(active = index == model.currentIndex.intValue) {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    },
                    onCheckedChange = {
                        model.currentIndex.intValue = index
                    },
                    checked = index == model.currentIndex.intValue
                ) {
                    Text(text = label, softWrap = true, maxLines = 1)
                }
            }
        }

        when (model.currentIndex.intValue) {
            0 -> ShowFeed(model)
        }
    }

    @Composable
    fun ShowFeed(model: ActivitiesScreenModel) {
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
                            userId = item.friendId
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
                            userId = item.friendId
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
                            userId = item.friendId
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
                            userId = item.friendId
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
                            userId = item.friendId
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
                            userId = item.friendId
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
                            userId = item.friendId
                        )
                    }
                }
            }
        }
    }
}