package cc.sovellus.vrcaa.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.api.vrchat.models.Worlds
import cc.sovellus.vrcaa.ui.components.layout.HorizontalRow
import cc.sovellus.vrcaa.ui.components.layout.RoundedRowItem
import cc.sovellus.vrcaa.ui.components.layout.WorldRow
import cc.sovellus.vrcaa.ui.models.home.HomeModel
import cc.sovellus.vrcaa.ui.models.home.HomeModel.HomeState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class HomeScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { HomeModel(context) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is HomeState.Loading -> LoadingIndicatorScreen().Content()
            is HomeState.Result -> DisplayHome(
                result.friends,
                result.lastVisitedWorlds,
                result.featuredWorlds,
            )

            else -> {}
        }
    }

    @Composable
    fun DisplayHome(
        friendsFlow: StateFlow<MutableList<LimitedUser>>,
        lastVisitedFlow: StateFlow<MutableList<World>>,
        featuredWorlds: Worlds?,
    ) {
        val friends = friendsFlow.collectAsState().value
        val lastVisited = lastVisitedFlow.collectAsState().value

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

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
                    items(
                        filteredFriends.size,
                        key = { UUID.randomUUID() }
                    ) {
                        val friend = filteredFriends[it]
                        RoundedRowItem(
                            name = friend.displayName,
                            url = friend.userIcon.ifEmpty { friend.imageUrl },
                            status = friend.status,
                            onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }

            item {
                HorizontalRow(
                    title = stringResource(R.string.home_recently_visited)
                ) {
                    items(
                        lastVisited.size,
                    ) {
                        val world = lastVisited[it]
                        WorldRow(
                            name = world.name,
                            url = world.imageUrl,
                            count = world.occupants,
                            onClick = { navigator.parent?.parent?.push(WorldInfoScreen(world.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }

            item {
                HorizontalRow(
                    title = stringResource(R.string.home_offline_friends)
                ) {
                    val filteredFriends = friends.filter { it.location == "offline" }
                    items(
                        filteredFriends.size,
                        key = { UUID.randomUUID() }
                    ) {
                        val friend = filteredFriends[it]
                        WorldRow(
                            name = friend.displayName,
                            url = friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl },
                            count = null,
                            onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }

            item {
                if (featuredWorlds == null) {
                    Toast.makeText(
                        context,
                        stringResource(R.string.home_failed_to_fetch_friends_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    HorizontalRow(
                        title = stringResource(R.string.home_featured_worlds)
                    ) {
                        items(
                            featuredWorlds.size,
                            key = { item -> featuredWorlds[item].id }
                        ) {
                            val world = featuredWorlds[it]
                            WorldRow(
                                name = world.name,
                                url = world.imageUrl,
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