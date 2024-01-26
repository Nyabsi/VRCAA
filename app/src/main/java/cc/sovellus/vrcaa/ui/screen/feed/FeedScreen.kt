package cc.sovellus.vrcaa.ui.screen.feed

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.manager.FeedManager

class FeedScreen : Screen {

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
                key = { item -> feed.value[item].feedId })
            {
                val item = feed.value[it]
                when(item.type) {
                    FeedManager.FeedType.FRIEND_FEED_ONLINE -> {
                        ListItem(
                            headlineContent = { Text("${item.friendName} is has come online.") },
                            overlineContent = { Text(item.type.name) },
                            supportingContent = { },
                            leadingContent = { },
                            trailingContent = { },
                        )
                    }
                    FeedManager.FeedType.FRIEND_FEED_OFFLINE -> {
                        ListItem(
                            headlineContent = { Text("${item.friendName} has gone offline.") },
                            overlineContent = { Text(item.type.name) },
                            supportingContent = { },
                            leadingContent = { },
                            trailingContent = { },
                        )
                    }
                    FeedManager.FeedType.FRIEND_FEED_LOCATION -> {
                        ListItem(
                            headlineContent = { Text("${item.friendName} went to ${item.travelDestination}.") },
                            overlineContent = { Text(item.type.name) },
                            supportingContent = { },
                            leadingContent = { },
                            trailingContent = { },
                        )
                    }
                    else -> { /* Unhandled */ }
                }
            }
        }
    }
}