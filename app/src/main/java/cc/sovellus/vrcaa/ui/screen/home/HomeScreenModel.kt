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

package cc.sovellus.vrcaa.ui.screen.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.CacheManager.WorldCache
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.ui.screen.home.HomeScreenModel.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeScreenModel : StateScreenModel<HomeState>(HomeState.Init) {

    sealed class HomeState {
        data object Init : HomeState()
        data object Loading : HomeState()
        data object Result : HomeState()
    }

    private var friendsListFlow = MutableStateFlow(emptyList<Friend>())
    private var friendsList = friendsListFlow.asStateFlow()

    val onlineFriends: StateFlow<List<Friend>> = friendsList.map { list ->
        list.filter { it.platform != "web" && it.platform.isNotEmpty() }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val friendsByLocation: StateFlow<Map<String, List<Friend>>> = friendsList.map { list ->
        list.filter { it.location.contains("wrld_") }.groupBy { it.location }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val offlineFriends: StateFlow<List<Friend>> = friendsList.map { list ->
        list.filter { it.platform.isEmpty() }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private var recentlyVisitedFlow = MutableStateFlow(emptyList<WorldCache>())
    var recentlyVisited = recentlyVisitedFlow.asStateFlow()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: List<Friend>) {
            friendsListFlow.update { friends }
        }
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun updateRecentlyVisitedWorlds(worlds: List<WorldCache>) {
            recentlyVisitedFlow.update { worlds }
        }

        override fun startCacheRefresh() {
            mutableState.value = HomeState.Loading
        }

        override fun endCacheRefresh() {
            fetchContent()
            mutableState.value = HomeState.Result
        }
    }

    init {
        mutableState.value = HomeState.Loading
        FriendManager.addListener(listener)
        CacheManager.addListener(cacheListener)

        if (CacheManager.isBuilt()) {
            fetchContent()
            mutableState.value = HomeState.Result
        } else {
            mutableState.value = HomeState.Loading
        }
    }

    private fun fetchContent() {
        friendsListFlow.value = FriendManager.getFriends()
        recentlyVisitedFlow.value = CacheManager.getRecentWorlds()
    }

}
