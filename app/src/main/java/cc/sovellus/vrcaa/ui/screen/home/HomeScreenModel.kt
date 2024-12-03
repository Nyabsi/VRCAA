package cc.sovellus.vrcaa.ui.screen.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.CacheManager.WorldCache
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    data object Init : HomeState()
    data object Loading : HomeState()
    data object Result : HomeState()
}

class HomeScreenModel : StateScreenModel<HomeState>(HomeState.Init) {

    private var friendsListFlow = MutableStateFlow(mutableStateListOf<Friend>())
    var friendsList = friendsListFlow.asStateFlow()

    private var recentlyVisitedFlow = MutableStateFlow(mutableStateListOf<WorldCache>())
    var recentlyVisited = recentlyVisitedFlow.asStateFlow()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<Friend>) {
            friendsListFlow.value = friends.toMutableStateList()
        }
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun recentlyVisitedUpdated(worlds: MutableList<WorldCache>) {
            recentlyVisitedFlow.value = worlds.toMutableStateList()
        }

        override fun startCacheRefresh() {
            mutableState.value = HomeState.Loading
        }

        override fun endCacheRefresh() {
            fetchContent()
            mutableState.value = HomeState.Result
        }

        override fun profileUpdated(profile: User) {}
    }

    init {
        mutableState.value = HomeState.Loading
        FriendManager.addFriendListener(listener)
        CacheManager.addListener(cacheListener)

        // BUG: this will be "true" before the initial screen loads
        // set "HomeState" to Loading with startCacheRefresh
        // is used as an temporary mitigation
        if (!CacheManager.isRefreshing()) {
            fetchContent()
            mutableState.value = HomeState.Result
        }
    }

    private fun fetchContent() {
        screenModelScope.launch {
            friendsListFlow.value = FriendManager.getFriends().toMutableStateList()
            recentlyVisitedFlow.value = CacheManager.getRecent().toMutableStateList()
        }
    }
}
