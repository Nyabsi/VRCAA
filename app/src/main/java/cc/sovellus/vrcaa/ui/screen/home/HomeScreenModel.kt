package cc.sovellus.vrcaa.ui.screen.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.VRChatCache
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.manager.ApiManager.cache
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel : ScreenModel {

    private var friendsListFlow = MutableStateFlow(listOf<Friend>())
    var friendsList = friendsListFlow.asStateFlow()

    private var recentlyVisitedFlow = MutableStateFlow(listOf<World>())
    var recentlyVisited = recentlyVisitedFlow.asStateFlow()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<Friend>) {
            friendsListFlow.value = friends.toList()
        }
    }

    private val cacheListener = object : VRChatCache.CacheListener {
        override fun updatedLastVisited(worlds: MutableList<World>) {
            recentlyVisitedFlow.value = worlds.toList()
        }

        override fun initialCacheCreated() {
            fetchContent()
            cache.isCachedLoaded = true
        }
    }

    init {
        FriendManager.addFriendListener(listener)
        cache.setCacheListener(cacheListener)

        if (cache.isCachedLoaded) {
            fetchContent()
        }
    }

    private fun fetchContent() {
        screenModelScope.launch {
            friendsListFlow.value = FriendManager.getFriends()
            recentlyVisitedFlow.value = cache.getRecent()
        }
    }
}