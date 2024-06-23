package cc.sovellus.vrcaa.ui.models.home

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
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

class HomeModel(
    private val context: Context
) : StateScreenModel<HomeModel.HomeState>(HomeState.Init) {

    sealed class HomeState {
        data object Init : HomeState()
        data object Loading : HomeState()
        data class Result(
            val friends: StateFlow<MutableList<LimitedUser>>,
            val lastVisitedWorlds: StateFlow<MutableList<World>>,
            val featuredWorlds: Worlds?
        ) : HomeState()
    }

    private var featuredWorlds: Worlds? = null

    private var friendsStateFlow = MutableStateFlow(mutableListOf<LimitedUser>())
    var friends = friendsStateFlow.asStateFlow()

    private var lastVisitedStateFlow = MutableStateFlow(mutableListOf<World>())
    var lastVisitedWorlds = lastVisitedStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

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
    }

    init {
        mutableState.value = HomeState.Loading
        fetchContent()
    }

    private fun fetchContent() {
        screenModelScope.launch {

            FriendManager.addFriendListener(listener)
            friendsStateFlow.update { FriendManager.getFriends() }

            cache.setCacheListener(cacheListener)
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