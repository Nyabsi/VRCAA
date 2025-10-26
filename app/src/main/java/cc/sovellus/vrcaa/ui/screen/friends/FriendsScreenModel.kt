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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class FriendsScreenModel : StateScreenModel<FriendsState>(FriendsState.Init) {

    sealed class FriendsState {
        data object Init : FriendsState()
        data object Loading : FriendsState()
        data object Result : FriendsState()
    }

    private var friendsStateFlow = MutableStateFlow(listOf<Friend>())
    private var friends = friendsStateFlow.asStateFlow()

    data class FriendsBuckets(
        val favoriteFriends: List<Friend> = emptyList(),
        val favoriteFriendsInInstances: List<Friend> = emptyList(),
        val favoriteFriendsOffline: List<Friend> = emptyList(),
        val friendsOnWebsite: List<Friend> = emptyList(),
        val friendsOnline: List<Friend> = emptyList(),
        val friendsInInstances: List<Friend> = emptyList(),
        val offlineFriends: List<Friend> = emptyList(),
    )

    private fun computeBuckets(all: List<Friend>): FriendsBuckets {
        val favorites = all.filter { FavoriteManager.isFavorite("friend", it.id) }
        val nonFavorites = all - favorites.toSet()

        return FriendsBuckets(
            favoriteFriends = favorites
                .filter { !it.location.contains("wrld_") && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            favoriteFriendsInInstances = favorites
                .filter { it.location.contains("wrld_") && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            favoriteFriendsOffline = favorites
                .filter { it.platform.isEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            friendsOnWebsite = nonFavorites
                .filter { it.platform == "web" }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            friendsOnline = nonFavorites
                .filter { !it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            friendsInInstances = nonFavorites
                .filter { it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            offlineFriends = nonFavorites
                .filter { it.platform.isEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) }
        )
    }

    @OptIn(FlowPreview::class)
    private val friendsBuckets = friends
        .debounce(100)
        .map { all ->
            withContext(Dispatchers.Default) {
                computeBuckets(all)
            }
        }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(),
            FriendsBuckets()
        )

    val favoriteFriends get() = friendsBuckets.map { it.favoriteFriends }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val favoriteFriendsInInstances get() = friendsBuckets.map { it.favoriteFriendsInInstances }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val favoriteFriendsOffline get() = friendsBuckets.map { it.favoriteFriendsOffline }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val friendsOnWebsite get() = friendsBuckets.map { it.friendsOnWebsite }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val friendsOnline get() = friendsBuckets.map { it.friendsOnline }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val friendsInInstances get() = friendsBuckets.map { it.friendsInInstances }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val offlineFriends get() = friendsBuckets.map { it.offlineFriends }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())

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