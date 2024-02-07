package cc.sovellus.vrcaa.ui.screen.home

import android.content.Context
import androidx.compose.runtime.mutableLongStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.models.Avatar
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.api.models.World
import cc.sovellus.vrcaa.helper.api
import kotlinx.coroutines.launch
import java.time.Clock

class HomeScreenModel(
    private val context: Context
) : StateScreenModel<HomeScreenModel.HomeState>(HomeState.Init) {

    sealed class HomeState {
        data object Init : HomeState()
        data object Loading : HomeState()
        data class Result(
            val friends: MutableList<LimitedUser>,
            val lastVisited: MutableList<World>,
            val featuredAvatars: MutableList<Avatar>,
            val offlineFriends: MutableList<LimitedUser>,
            val featuredWorlds: MutableList<World>
        ) : HomeState()
    }

    private var friends = mutableListOf<LimitedUser>()
    private var lastVisited = mutableListOf<World>()
    private var featuredAvatars = mutableListOf<Avatar>()
    private var offlineFriends = mutableListOf<LimitedUser>()
    private var featuredWorlds = mutableListOf<World>()

    val lastClickElapsed = mutableLongStateOf(Clock.systemUTC().millis())

    init {
        mutableState.value = HomeState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {

            context.api.get().getFriends()?.let { friends = it }
            context.api.get().getRecentWorlds()?.let { lastVisited = it }
            context.api.get().getAvatars()?.let { featuredAvatars = it }
            context.api.get().getFriends(true)?.let { offlineFriends = it }
            context.api.get().getWorlds()?.let { featuredWorlds = it }

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