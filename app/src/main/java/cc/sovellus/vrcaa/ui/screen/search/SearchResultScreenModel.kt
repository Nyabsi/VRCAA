package cc.sovellus.vrcaa.ui.screen.search

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.avatars.models.JustHPartyAvatars
import cc.sovellus.vrcaa.api.avatars.providers.JustHPartyProvider
import cc.sovellus.vrcaa.api.models.Users
import cc.sovellus.vrcaa.api.models.LimitedWorlds
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    context: Context,
    private val query: String
) : StateScreenModel<SearchResultScreenModel.SearchState>(SearchState.Init) {
    private val api = ApiContext(context)
    private val avatarProvider = JustHPartyProvider()

    sealed class SearchState {
        data object Init : SearchState()
        data object Loading : SearchState()
        data class Result(
            val foundWorlds: MutableList<LimitedWorlds.LimitedWorldItem>,
            val foundUsers: MutableList<Users.UsersItem>,
            val foundAvatars: MutableList<JustHPartyAvatars.JustHPartyAvatarsItem>
        ) : SearchState()
    }

    private var foundWorlds = mutableListOf<LimitedWorlds.LimitedWorldItem>()
    private var foundUsers = mutableListOf<Users.UsersItem>()
    private var foundAvatars = mutableListOf<JustHPartyAvatars.JustHPartyAvatarsItem>()

    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = SearchState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {
            api.getWorlds(
                query = query,
                featured = false,
                n = 50,
                sort = "relevance"
            )?.let { foundWorlds = it }

            api.getUsers(
                username = query,
                n = 50
            )?.let { foundUsers = it }

            avatarProvider.search(
                "search",
                query,
                5000 // Not used
            )?.let { foundAvatars = it }

            mutableState.value = SearchState.Result(foundWorlds, foundUsers, foundAvatars)
        }
    }
}