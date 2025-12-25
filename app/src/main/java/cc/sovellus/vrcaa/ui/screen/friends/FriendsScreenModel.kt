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

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel.FriendsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class FriendsScreenModel : StateScreenModel<FriendsState>(FriendsState.Init) {

    sealed class FriendsState {
        data object Init : FriendsState()
        data object Loading : FriendsState()
        data object Result : FriendsState()
    }

    private var friendsStateFlow = MutableStateFlow(listOf<Friend>())
    private var friends = friendsStateFlow.asStateFlow()

    val favoriteFriends = friends.map { friend ->
        friend.filter { FavoriteManager.isFavorite("friend", it.id) && !it.location.contains("wrld_") && it.platform.isNotEmpty() }.sortedBy { StatusHelper.getStatusFromString(it.status) }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val favoriteFriendsInInstances = friends.map { friend ->
        friend.filter { FavoriteManager.isFavorite("friend", it.id) && it.location.contains("wrld_") && it.platform.isNotEmpty() }.sortedBy { StatusHelper.getStatusFromString(it.status) }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val favoriteFriendsOffline = friends.map { friend ->
        friend.filter { FavoriteManager.isFavorite("friend", it.id) && it.platform.isEmpty() }.sortedBy { StatusHelper.getStatusFromString(it.status) }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val friendsOnWebsite = friends.map { friend ->
        friend.filter { !FavoriteManager.isFavorite("friend", it.id) && it.platform == "web" }.sortedBy { StatusHelper.getStatusFromString(it.status) }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val friendsOnline = friends.map { friend ->
        friend.filter { !FavoriteManager.isFavorite("friend", it.id) && !it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }.sortedBy { StatusHelper.getStatusFromString(it.status) }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val friendsInInstances = friends.map { friend ->
        friend.filter { !FavoriteManager.isFavorite("friend", it.id) && it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }.sortedBy { StatusHelper.getStatusFromString(it.status) }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val offlineFriends = friends.map { friend ->
        friend.filter { !FavoriteManager.isFavorite("friend", it.id) && it.platform.isEmpty() }.sortedBy { StatusHelper.getStatusFromString(it.status) }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: List<Friend>) {
            friendsStateFlow.update { friends }
        }
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun startCacheRefresh() {
            mutableState.value = FriendsState.Loading
        }

        override fun endCacheRefresh() {
            friendsStateFlow.value = FriendManager.getFriends().toMutableStateList()
            mutableState.value = FriendsState.Result
        }
    }

    init {
        mutableState.value = FriendsState.Loading
        FriendManager.addListener(listener)
        CacheManager.addListener(cacheListener)

        if (CacheManager.isBuilt()) {
            friendsStateFlow.value = FriendManager.getFriends().toMutableStateList()
            mutableState.value = FriendsState.Result
        } else {
            mutableState.value = FriendsState.Loading
        }
    }
}