package cc.sovellus.vrcaa.ui.models.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Friends
import cc.sovellus.vrcaa.api.vrchat.models.Worlds
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.delay
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

    val isRefreshing = mutableStateOf(false)

    init {
        mutableState.value = HomeState.Loading
        fetchContent()
    }

    private fun fetchContent() {
        screenModelScope.launch {

            friends = api?.getFriends()
            lastVisited = api?.getRecentWorlds()
            offlineFriends = api?.getFriends(true)
            featuredWorlds = api?.getWorlds()

            mutableState.value = HomeState.Result(
                friends = friends,
                lastVisited = lastVisited,
                offlineFriends = offlineFriends,
                featuredWorlds = featuredWorlds
            )
        }
    }

    fun refreshHome(context: Context) {
        screenModelScope.launch {

            isRefreshing.value = true
            fetchContent()
            delay(500)
            isRefreshing.value = false

            Toast.makeText(
                context,
                context.getString(R.string.home_toast_updated_info),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}