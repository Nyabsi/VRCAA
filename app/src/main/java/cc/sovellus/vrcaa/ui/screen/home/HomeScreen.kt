package cc.sovellus.vrcaa.ui.screen.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.ApiManager.cache
import cc.sovellus.vrcaa.ui.components.layout.HorizontalRow
import cc.sovellus.vrcaa.ui.components.layout.RoundedRowItem
import cc.sovellus.vrcaa.ui.components.layout.WorldRow
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen

class HomeScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val model = navigator.rememberNavigatorScreenModel { HomeScreenModel(context) }

        val friends = model.friendsList.collectAsState().value
        val recent = model.recentlyVisited.collectAsState().value

        if (friends.isEmpty() || recent.isEmpty()) {
            LoadingIndicatorScreen().Content()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                item {
                    HorizontalRow(
                        title = stringResource(R.string.home_active_friends)
                    ) {
                        val filteredFriends = friends.filter { it.location != "offline" }
                        items(filteredFriends.sortedBy { StatusHelper.getStatusFromString(it.status) }, key = { it.id }) { friend ->
                            RoundedRowItem(
                                name = friend.displayName,
                                url = friend.userIcon.ifEmpty { friend.currentAvatarImageUrl },
                                status = friend.status,
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    HorizontalRow(
                        title = stringResource(R.string.home_recently_visited)
                    ) {
                        items(recent, key = { it.id }) { world ->
                            WorldRow(
                                name = world.name,
                                url = world.thumbnailUrl,
                                count = world.occupants,
                                onClick = { navigator.parent?.parent?.push(WorldInfoScreen(world.id)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    HorizontalRow(
                        title = stringResource(R.string.home_offline_friends)
                    ) {
                        val filteredFriends = friends.filter { it.location == "offline" }
                        items(filteredFriends, key = { it.id }) { friend ->
                            WorldRow(
                                name = friend.displayName,
                                url = friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl },
                                count = null,
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    HorizontalRow(
                        title = stringResource(R.string.home_friend_locations)
                    ) {
                        val friendLocations = friends.filter { it.location.contains("wrld_") }
                        items(friendLocations, key = { it.id }) { friend ->
                            val world = cache.getWorld(friend.location.split(':')[0])
                            if (world != null) {
                                WorldRow(
                                    name = world.name,
                                    url = world.thumbnailUrl,
                                    count = world.occupants,
                                    onClick = { navigator.parent?.parent?.push(WorldInfoScreen(world.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}