package cc.sovellus.vrcaa.ui.screen.home

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Avatars
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.LimitedWorlds
import kotlinx.coroutines.launch

class HomeScreenModel(
    context: Context
) : StateScreenModel<HomeScreenModel.HomeState>(HomeState.Init) {

    private val api: ApiContext = ApiContext(context)

    sealed class HomeState {
        data object Init : HomeState()
        data object Loading : HomeState()
        data class Result(
            val friends: MutableList<Friends.FriendsItem>,
            val lastVisited: MutableList<LimitedWorlds.LimitedWorldItem>,
            val featuredAvatars: MutableList<Avatars.AvatarsItem>,
            val offlineFriends: MutableList<Friends.FriendsItem>,
            val featuredWorlds: MutableList<LimitedWorlds.LimitedWorldItem>
        ) : HomeState()
    }

    private var friends = mutableListOf<Friends.FriendsItem>()
    private var lastVisited = mutableListOf<LimitedWorlds.LimitedWorldItem>()
    private var featuredAvatars = mutableListOf<Avatars.AvatarsItem>()
    private var offlineFriends = mutableListOf<Friends.FriendsItem>()
    private var featuredWorlds = mutableListOf<LimitedWorlds.LimitedWorldItem>()

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