package cc.sovellus.vrcaa.ui.screen.search

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.search.SearchAvatar
import cc.sovellus.vrcaa.api.search.avtrdb.AvtrDbProvider
import cc.sovellus.vrcaa.api.search.justhparty.JustHPartyProvider
import cc.sovellus.vrcaa.api.vrchat.http.models.Group
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.extension.avatarProvider
import cc.sovellus.vrcaa.extension.avatarsAmount
import cc.sovellus.vrcaa.extension.sortWorlds
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    private val query: String
) : StateScreenModel<SearchResultScreenModel.SearchState>(SearchState.Init) {

    sealed class SearchState {
        data object Init : SearchState()
        data object Loading : SearchState()
        data class Result(
            val worlds: ArrayList<World>,
            val users: MutableList<LimitedUser>,
            val avatars: ArrayList<SearchAvatar>,
            val groups: ArrayList<Group>
        ) : SearchState()
    }

    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", Context.MODE_PRIVATE)

    private val avtrDbProvider = AvtrDbProvider()
    private val justHPartyProvider = JustHPartyProvider()

    private var worlds: ArrayList<World> = arrayListOf()
    private var users: MutableList<LimitedUser> = arrayListOf()
    private var avatars: ArrayList<SearchAvatar> = arrayListOf()
    private var groups: ArrayList<Group> = arrayListOf()

    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = SearchState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {

            App.setLoadingText(R.string.loading_text_worlds)

            worlds = api.worlds.fetchWorldsByName(
                query,
                preferences.sortWorlds
            )

            App.setLoadingText(R.string.loading_text_users)

            users = api.users.fetchUsersByName(query)

            App.setLoadingText(R.string.loading_text_avatars)

            when (preferences.avatarProvider) {
                "avtrdb" -> {
                    avatars = avtrDbProvider.search(query, preferences.avatarsAmount)
                }
                "justhparty" -> {
                    // cannot implement limits...
                    avatars = justHPartyProvider.search(query)
                }
            }

            App.setLoadingText(R.string.loading_text_groups)

            groups = api.groups.fetchGroupsByName(query)

            mutableState.value = SearchState.Result(worlds, users, avatars, groups)
        }
    }
}