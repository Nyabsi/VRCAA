package cc.sovellus.vrcaa.ui.screen.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.VRChatCache
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.api.vrchat.models.Worlds
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.ApiManager.cache
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class HomeState {
    data object Init : HomeState()
    data object Loading : HomeState()
    data class Result(
        val friends: StateFlow<MutableList<LimitedUser>>,
        val lastVisitedWorlds: StateFlow<MutableList<World>>,
        val featuredWorlds: Worlds?
    ) : HomeState()
}

class HomeModel : StateScreenModel<HomeState>(HomeState.Init) {

    private var featuredWorlds: Worlds? = null

    private var friendsStateFlow = MutableStateFlow(mutableListOf<LimitedUser>())
    var friends = friendsStateFlow.asStateFlow()

    private var lastVisitedStateFlow = MutableStateFlow(mutableListOf<World>())
    private var lastVisitedWorlds = lastVisitedStateFlow.asStateFlow()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<LimitedUser>) {
            screenModelScope.launch {
                friendsStateFlow.update { friends }
            }
        }
    }

    private val cacheListener = object : VRChatCache.CacheListener {
        override fun updatedLastVisited(worlds: MutableList<World>) {
            lastVisitedStateFlow.update { worlds }
        }

        override fun initialCacheCreated() {
            fetchContent()
        }
    }

    init {
        mutableState.value = HomeState.Loading
        cache.setCacheListener(cacheListener)
    }

    private fun fetchContent() {
        screenModelScope.launch {

            FriendManager.addFriendListener(listener)
            friendsStateFlow.update { FriendManager.getFriends() }

            lastVisitedStateFlow.update { cache.getRecent() }
            featuredWorlds = api.getWorlds()

            mutableState.value = HomeState.Result(
                friends = friends,
                lastVisitedWorlds = lastVisitedWorlds,
                featuredWorlds = featuredWorlds
            )
        }
    }
}