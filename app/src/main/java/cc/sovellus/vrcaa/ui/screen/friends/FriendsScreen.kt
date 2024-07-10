package cc.sovellus.vrcaa.ui.screen.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.ui.components.layout.FriendItem
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import java.util.UUID

class FriendsScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val model = navigator.rememberNavigatorScreenModel { FriendsScreenModel() }
        val friends = model.friends.collectAsState()

        val options = stringArrayResource(R.array.friend_selection_options)
        val icons = listOf(Icons.Filled.Star, Icons.Filled.Person, Icons.Filled.Web, Icons.Filled.PersonOff)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                0 -> ShowFriendsFavorite(friends)
                1 -> ShowFriends(friends)
                2 -> ShowFriendsOnWebsite(friends)
                3 -> ShowFriendsOffline(friends)
            }
        }
    }

    @Composable
    fun ShowFriendsFavorite(
        friends: State<List<Friend>>
    ) {
        if (friends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                items(friends.value.sortedBy { StatusHelper.getStatusFromString(it.status) }, key = { it.id }) { friend ->
                    if (friend.isFavorite && friend.location != "offline") {
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ShowFriendsOnWebsite(
        friends: State<List<Friend>>
    ) {
        if (friends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                items(friends.value.sortedBy { StatusHelper.getStatusFromString(it.status) }, key = { it.id }) { friend ->
                    if (friend.location == "offline" && StatusHelper.getStatusFromString(friend.status) != StatusHelper.Status.Offline) {
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ShowFriends(
        friends: State<List<Friend>>
    ) {
        if (friends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                items(friends.value.sortedBy { StatusHelper.getStatusFromString(it.status) }, key = { it.id }) { friend ->
                    if (!friend.isFavorite && friend.location != "offline" && StatusHelper.getStatusFromString(friend.status) != StatusHelper.Status.Offline) {
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ShowFriendsOffline(
        friends: State<List<Friend>>
    ) {
        if (friends.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val navigator = LocalNavigator.currentOrThrow
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(1.dp),
                state = rememberLazyListState()
            ) {
                items(friends.value.sortedBy { StatusHelper.getStatusFromString(it.status) }, key = { it.id }) { friend ->
                    if (friend.location == "offline" && StatusHelper.getStatusFromString(friend.status) == StatusHelper.Status.Offline) {
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
            }
        }
    }
}