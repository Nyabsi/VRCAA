package cc.sovellus.vrcaa.ui.screen.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Avatars
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.Worlds
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val api: ApiContext
) : StateScreenModel<HomeScreenModel.HomeState>(HomeState.Init) {

    sealed class HomeState {
        data object Init : HomeState()
        data object Loading : HomeState()
        data class Result(
            val friends: MutableList<Friends.FriendsItem>,
            val lastVisited: MutableList<Worlds.WorldItem>,
            val featuredAvatars: MutableList<Avatars.AvatarsItem>,
            val offlineFriends: MutableList<Friends.FriendsItem>,
            val featuredWorlds: MutableList<Worlds.WorldItem>
        ) : HomeState()
    }

    private var friends = mutableListOf<Friends.FriendsItem>()
    private var lastVisited = mutableListOf<Worlds.WorldItem>()
    private var featuredAvatars = mutableListOf<Avatars.AvatarsItem>()
    private var offlineFriends = mutableListOf<Friends.FriendsItem>()
    private var featuredWorlds = mutableListOf<Worlds.WorldItem>()

    init {
        mutableState.value = HomeState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {

            api.getFriends()?.let { friends = it }
            api.getRecentWorlds()?.let { lastVisited = it }
            api.getAvatars()?.let { featuredAvatars = it }
            api.getFriends(true)?.let { offlineFriends = it }
            api.getWorlds()?.let { featuredWorlds = it }

            mutableState.value = HomeState.Result(
                friends = friends,
                lastVisited = lastVisited,
                featuredAvatars = featuredAvatars,
                offlineFriends = offlineFriends,
                featuredWorlds = featuredWorlds
            )
        }
    }
}