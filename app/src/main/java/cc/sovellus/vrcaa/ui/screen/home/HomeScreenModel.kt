package cc.sovellus.vrcaa.ui.screen.home

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.models.Friends
import cc.sovellus.vrcaa.api.http.models.Worlds
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val context: Context
) : StateScreenModel<HomeScreenModel.HomeState>(HomeState.Init) {

    sealed class HomeState {
        data object Init : HomeState()
        data object Loading : HomeState()
        data class Result(
            val friends: Friends?,
            val lastVisited: Worlds?,
            val offlineFriends: Friends?,
            val featuredWorlds: Worlds?
        ) : HomeState()
    }

    private var friends: Friends? = null
    private var lastVisited: Worlds? = null
    private var offlineFriends: Friends? = null
    private var featuredWorlds: Worlds? = null

    init {
        mutableState.value = HomeState.Loading
        fetchContent()
    }

    private fun fetchContent() {
        screenModelScope.launch {

            // TODO: fetch friends directly from FriendManager instead.
            friends = api.getFriends()
            lastVisited = api.getRecentWorlds()
            offlineFriends = api.getFriends(true)
            featuredWorlds = api.getWorlds()

            mutableState.value = HomeState.Result(
                friends = friends,
                lastVisited = lastVisited,
                offlineFriends = offlineFriends,
                featuredWorlds = featuredWorlds
            )
        }
    }
}