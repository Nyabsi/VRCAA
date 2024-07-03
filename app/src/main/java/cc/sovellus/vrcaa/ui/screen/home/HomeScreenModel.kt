package cc.sovellus.vrcaa.ui.screen.home

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel : ScreenModel {

    private var featuredWorldsFlow = MutableStateFlow<List<World>>(listOf())
    var featuredWorlds = featuredWorldsFlow.asStateFlow()

    private var friendsListFlow = MutableStateFlow<List<LimitedUser>>(listOf())
    var friendsList = friendsListFlow.asStateFlow()

    private var recentlyVisitedFlow = MutableStateFlow<List<World>>(listOf())
    var recentlyVisited = recentlyVisitedFlow.asStateFlow()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: List<LimitedUser>) {
            friendsListFlow.value = friends
        }
    }

    private val cacheListener = object : VRChatCache.CacheListener {
        override fun updatedLastVisited(worlds: List<World>) {
            recentlyVisitedFlow.value = worlds
        }

        override fun initialCacheCreated() {
            fetchContent()
            cache.isCachedLoaded = true
        }
    }

    init {
        cache.setCacheListener(cacheListener)
        if (cache.isCachedLoaded) {
            fetchContent()
        }
    }

    private fun fetchContent() {
        screenModelScope.launch {
            FriendManager.addFriendListener(listener)
            friendsListFlow.value = FriendManager.getFriends()
            recentlyVisitedFlow.value = cache.getRecent()
            featuredWorldsFlow.value = api.getWorlds()
        }
    }
}