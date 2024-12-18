package cc.sovellus.vrcaa.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.ui.components.layout.HorizontalRow
import cc.sovellus.vrcaa.ui.components.layout.RoundedRowItem
import cc.sovellus.vrcaa.ui.components.layout.RowItem
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen

class HomeScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { HomeScreenModel() }

        val state by model.state.collectAsState()

        when (state) {
            is HomeState.Loading -> LoadingIndicatorScreen().Content()
            is HomeState.Result -> ShowScreen(model)
            else -> {}
        }
    }

    @Composable
    fun ShowScreen(model: HomeScreenModel) {

        val navigator = LocalNavigator.currentOrThrow
        val friends = model.friendsList.collectAsState().value
        val recent = model.recentlyVisited.collectAsState().value

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            item {
                val onlineFriends = friends.filter { it.platform.isNotEmpty() && it.platform != "web" }
                if (onlineFriends.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_active_friends),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            item {
                                Text(text = stringResource(R.string.result_not_found))
                            }
                        }
                    )
                } else {
                    HorizontalRow(
                        title = stringResource(R.string.home_active_friends)
                    ) {
                        items(
                            onlineFriends.sortedBy { StatusHelper.getStatusFromString(it.status) },
                            key = { it.id }) { friend ->
                            RoundedRowItem(
                                name = friend.displayName,
                                url = friend.userIcon.ifEmpty { friend.currentAvatarImageUrl },
                                status = friend.status,
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                if (recent.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_recently_visited),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .height(190.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            item {
                                Text(text = stringResource(R.string.result_not_found))
                            }
                        }
                    )
                } else {
                    HorizontalRow(
                        title = stringResource(R.string.home_recently_visited)
                    ) {
                        items(recent, key = { it.id }) { world ->
                            RowItem(
                                name = world.name,
                                url = world.thumbnailUrl,
                                onClick = { navigator.parent?.parent?.push(WorldInfoScreen(world.id)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                val friendLocations = friends.filter { it.location.contains("wrld_") }
                if (friendLocations.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_friend_locations),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .height(190.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            item {
                                Text(text = stringResource(R.string.result_not_found))
                            }
                        }
                    )
                } else {
                    HorizontalRow(
                        title = stringResource(R.string.home_friend_locations)
                    ) {
                        items(
                            friendLocations.distinctBy { it.location.split(':')[0] },
                            key = { it.id }) { friend ->
                            val world = CacheManager.getWorld(friend.location.split(':')[0])
                            RowItem(
                                name = world.name,
                                url = world.thumbnailUrl,
                                onClick = { navigator.parent?.parent?.push(WorldInfoScreen(world.id)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                if (friendLocations.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_offline_friends),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .height(190.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            item {
                                Text(text = stringResource(R.string.result_not_found))
                            }
                        }
                    )
                } else {
                    HorizontalRow(
                        title = stringResource(R.string.home_offline_friends)
                    ) {
                        val filteredFriends = friends.filter { it.platform.isEmpty() }
                        items(filteredFriends, key = { it.id }) { friend ->
                            RowItem(
                                name = friend.displayName,
                                url = friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl },
                                onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}