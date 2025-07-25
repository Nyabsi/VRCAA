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

package cc.sovellus.vrcaa.ui.screen.friends

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.layout.FriendItem
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel.FriendsState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen

class FriendsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { FriendsScreenModel() }

        BackHandler(
            enabled = model.currentIndex.intValue != 0,
            onBack = {
                model.currentIndex.intValue = 0
            }
        )

        val state by model.state.collectAsState()

        when (state) {
            is FriendsState.Loading -> LoadingIndicatorScreen().Content()
            is FriendsState.Result -> ShowScreen(model)
            else -> {}
        }
    }

    @Composable
    fun ShowScreen(model: FriendsScreenModel)
    {
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
                            SegmentedButtonDefaults.Icon(
                                active = index == model.currentIndex.intValue,
                                activeContent = {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize).offset(y = 2.5.dp)
                                    )
                                },
                                inactiveContent = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = null,
                                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize).offset(y = 2.5.dp)
                                    )
                                }
                            )
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
        val favoriteFriends = friends.value.filter { FavoriteManager.isFavorite("friend", it.id) && !it.location.contains("wrld_") && it.platform.isNotEmpty() }
        val favoriteFriendsInInstances = friends.value.filter { FavoriteManager.isFavorite("friend", it.id) && it.location.contains("wrld_") && it.platform.isNotEmpty() }
        val favoriteFriendsOffline = friends.value.filter { FavoriteManager.isFavorite("friend", it.id) && it.platform.isEmpty() }

        if (favoriteFriends.isEmpty() && favoriteFriendsInInstances.isEmpty() && favoriteFriendsOffline.isEmpty()) {
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
                items(
                    favoriteFriendsInInstances.sortedBy { StatusHelper.getStatusFromString(it.status) }) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }

                if (favoriteFriendsInInstances.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }

                }

                items(
                    favoriteFriends.sortedBy { StatusHelper.getStatusFromString(it.status) }) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }

                if (favoriteFriendsOffline.isNotEmpty() && favoriteFriends.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }
                }

                items(
                    favoriteFriendsOffline.sortedBy { StatusHelper.getStatusFromString(it.status) }) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }
            }
        }
    }

    @Composable
    fun ShowFriendsOnWebsite(
        friends: State<List<Friend>>
    ) {
        val filteredFriends = friends.value.filter { !FavoriteManager.isFavorite("friend", it.id) && it.platform == "web" }
        if (filteredFriends.isEmpty()) {
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
                items(filteredFriends.sortedBy { StatusHelper.getStatusFromString(it.status)  }) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }
            }
        }
    }

    @Composable
    fun ShowFriends(
        friends: State<List<Friend>>
    ) {
        val filteredFriends = friends.value.filter { !FavoriteManager.isFavorite("friend", it.id) && !it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }
        val filteredFriendsInInstances = friends.value.filter { !FavoriteManager.isFavorite("friend", it.id) && it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }

        if (filteredFriends.isEmpty() && filteredFriendsInInstances.isEmpty()) {
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
                if (filteredFriendsInInstances.isNotEmpty())
                {
                    items(filteredFriendsInInstances.sortedBy { StatusHelper.getStatusFromString(it.status)  }) { friend ->
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                    item {
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }
                    items(filteredFriends.sortedBy { StatusHelper.getStatusFromString(it.status)  }) { friend ->
                        FriendItem(
                            friend = friend,
                            callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                } else {
                    items(filteredFriends.sortedBy { StatusHelper.getStatusFromString(it.status)  }) { friend ->
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
        val filteredFriends = friends.value.filter { !FavoriteManager.isFavorite("friend", it.id) && it.platform.isEmpty() }
        if (filteredFriends.isEmpty()) {
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
                items(filteredFriends.sortedBy { StatusHelper.getStatusFromString(it.status) }) { friend ->
                    FriendItem(
                        friend = friend,
                        callback = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                    )
                }
            }
        }
    }
}