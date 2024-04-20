package cc.sovellus.vrcaa.ui.models.search

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.justhparty.models.JustHPartyAvatars
import cc.sovellus.vrcaa.api.justhparty.JustHPartyProvider
import cc.sovellus.vrcaa.api.vrchat.models.Groups
import cc.sovellus.vrcaa.api.vrchat.models.Users
import cc.sovellus.vrcaa.api.vrchat.models.Worlds
import cc.sovellus.vrcaa.helper.searchFeaturedWorlds
import cc.sovellus.vrcaa.helper.sortWorlds
import cc.sovellus.vrcaa.helper.usersAmount
import cc.sovellus.vrcaa.helper.worldsAmount
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
            val worlds: Worlds?,
            val users: Users?,
            val avatars: JustHPartyAvatars?,
            val groups: Groups?
        ) : SearchState()
    }

    private var worlds: Worlds? = null
    private var users: Users? = null
    private var avatars: JustHPartyAvatars? = null
    private var groups: Groups? = null

    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = SearchState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {
            worlds = api?.getWorlds(
                query = query,
                featured = preferences.searchFeaturedWorlds,
                n = preferences.worldsAmount,
                sort = preferences.sortWorlds
            )

            users = api?.getUsers(
                username = query,
                n = preferences.usersAmount
            )

            avatars = avatarProvider.search(
                "search",
                query,
                5000 // Not used
            )

            groups = api?.getGroups(
                query,
                100 // TODO: shit.
            )

            mutableState.value = SearchState.Result(worlds, users, avatars, groups)
        }
    }
}