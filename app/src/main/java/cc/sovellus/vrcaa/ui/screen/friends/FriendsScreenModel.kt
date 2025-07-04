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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class FriendsState {
    data object Init : FriendsState()
    data object Loading : FriendsState()
    data object Result : FriendsState()
}

class FriendsScreenModel : StateScreenModel<FriendsState>(FriendsState.Init) {

    private var friendsStateFlow = MutableStateFlow(mutableStateListOf<Friend>())
    var friends = friendsStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<Friend>) {
            friendsStateFlow.value = friends.toMutableStateList()
        }
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun profileUpdated(profile: User) {}

        override fun startCacheRefresh() {}

        override fun endCacheRefresh() {
            friendsStateFlow.value = FriendManager.getFriends().toMutableStateList()
            mutableState.value = FriendsState.Result
        }

        override fun recentlyVisitedUpdated(worlds: MutableList<CacheManager.WorldCache>) {}
    }

    init {
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