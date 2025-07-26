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

package cc.sovellus.vrcaa.ui.screen.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteEditDialog
import cc.sovellus.vrcaa.ui.components.layout.FavoriteHorizontalRow
import cc.sovellus.vrcaa.ui.components.layout.RowItem
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen

class FavoritesScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { FavoritesScreenModel() }

        val state by model.state.collectAsState()

        when (state) {
            is FavoritesScreenModel.FavoriteState.Loading -> LoadingIndicatorScreen().Content()
            is FavoritesScreenModel.FavoriteState.Result -> ShowScreen(model)
            else -> {}
        }
    }

    @Composable
    fun ShowScreen(model: FavoritesScreenModel) {

        if (model.editDialogShown.value) {
            FavoriteEditDialog(
                model.currentSelectedGroup.value,
                model.currentSelectedIsFriend.value,
                onDismiss = {
                    model.editDialogShown.value = false
                    model.currentSelectedIsFriend.value = false
                },
                onConfirmation = {
                    model.editDialogShown.value = false
                    model.currentSelectedIsFriend.value = false
                }
            )
        }

        val worldList = model.worldList.collectAsState()
        val avatarList = model.avatarList.collectAsState()
        val friendList = model.friendList.collectAsState()

        val options = stringArrayResource(R.array.favorites_selection_options)
        val icons = listOf(Icons.Filled.Cabin, Icons.Filled.Person, Icons.Filled.Group)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
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

            Spacer(modifier = Modifier.padding(4.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                item {
                    when (model.currentIndex.intValue) {
                        0 -> ShowWorlds(model, worldList)
                        1 -> ShowAvatars(model, avatarList)
                        2 -> ShowFriends(model, friendList)
                    }
                }
            }
        }
    }

    @Composable
    fun ShowWorlds(
        model: FavoritesScreenModel,
        worldList: State<SnapshotStateMap<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        val sortedWorldList = worldList.value.toSortedMap(compareBy { it.substring(6).toInt() })
        sortedWorldList.forEach { item ->
            if (item.value.isNotEmpty()) {
                FavoriteHorizontalRow(
                    title = "${FavoriteManager.getDisplayNameFromTag(item.key)} (${FavoriteManager.getGroupMetadata(item.key)?.size ?: 0}/${FavoriteManager.getMaximumFavoritesFromTag(item.key)})",
                    onEdit = {
                        model.currentSelectedGroup.value = item.key
                        model.editDialogShown.value = true
                    }
                ) {
                    items(item.value) {
                        RowItem(name = it.name, url = it.thumbnailUrl) {
                            if (it.name != "???") {
                                navigator.parent?.parent?.push(WorldScreen(it.id))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }

    @Composable
    fun ShowAvatars(
        model: FavoritesScreenModel,
        avatarList: State<SnapshotStateMap<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        val sortedAvatarList = avatarList.value.toSortedMap(compareBy { it.substring(7).toInt() })
        sortedAvatarList.forEach { item ->
            if (item.value.isNotEmpty()) {
                FavoriteHorizontalRow(
                    title = "${FavoriteManager.getDisplayNameFromTag(item.key)} (${FavoriteManager.getGroupMetadata(item.key)?.size ?: 0}/${FavoriteManager.getMaximumFavoritesFromTag(item.key)})",
                    onEdit = {
                        model.currentSelectedGroup.value = item.key
                        model.editDialogShown.value = true
                    }
                ) {
                    items(item.value) {
                        RowItem(name = it.name, url = it.thumbnailUrl) {
                            if (it.name != "???") {
                                navigator.parent?.parent?.push(AvatarScreen(it.id))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }

    @Composable
    fun ShowFriends(
        model: FavoritesScreenModel,
        friendList: State<SnapshotStateMap<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        val sortedFriendList = friendList.value.toSortedMap(compareBy { it.substring(6).toInt() })
        sortedFriendList.forEach { item ->
            if (item.value.isNotEmpty()) {
                FavoriteHorizontalRow(
                    title = "${FavoriteManager.getDisplayNameFromTag(item.key)} (${FavoriteManager.getGroupMetadata(item.key)?.size ?: 0}/${FavoriteManager.getMaximumFavoritesFromTag(item.key)})",
                    onEdit = {
                        model.currentSelectedIsFriend.value = true
                        model.currentSelectedGroup.value = item.key
                        model.editDialogShown.value = true
                    }
                ) {
                    items(item.value) {
                        val user = FriendManager.getFriend(it.id)
                        user?.let {
                            RowItem(name = user.displayName, url = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl }) {
                                navigator.parent?.parent?.push(UserProfileScreen(it.id))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}