package cc.sovellus.vrcaa.ui.screen.search

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.justhparty.JustHPartyProvider
import cc.sovellus.vrcaa.api.vrchat.models.Group
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.extension.groupsAmount
import cc.sovellus.vrcaa.extension.searchFeaturedWorlds
import cc.sovellus.vrcaa.extension.sortWorlds
import cc.sovellus.vrcaa.extension.usersAmount
import cc.sovellus.vrcaa.extension.worldsAmount
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    context: Context,
    private val query: String
) : StateScreenModel<SearchResultScreenModel.SearchState>(SearchState.Init) {

    private val avatarProvider = JustHPartyProvider()
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", Context.MODE_PRIVATE)

    sealed class SearchState {
        data object Init : SearchState()
        data object Loading : SearchState()
        data class Result(
            val worlds: ArrayList<World>?,
            val users: MutableList<LimitedUser>?,
            val avatars: ArrayList<JustHPartyProvider.Avatar>?,
            val groups: ArrayList<Group>?
        ) : SearchState()
    }

    private var worlds: ArrayList<World>? = null
    private var users: MutableList<LimitedUser>? = null
    private var avatars: ArrayList<JustHPartyProvider.Avatar>? = null
    private var groups: ArrayList<Group>? = null

    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = SearchState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {
            worlds = api.getWorlds(
                query,
                preferences.worldsAmount,
                preferences.searchFeaturedWorlds,
                preferences.sortWorlds
            )

            users = api.getUsers(
                username = query,
                preferences.usersAmount
            )

            avatars = avatarProvider.search(
                "search",
                query,
                5000 // Not used
            )

            groups = api.getGroups(
                query,
                preferences.groupsAmount
            )

            mutableState.value = SearchState.Result(worlds, users, avatars, groups)
        }
    }
}