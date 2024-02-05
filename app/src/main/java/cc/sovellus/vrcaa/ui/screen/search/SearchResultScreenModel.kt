package cc.sovellus.vrcaa.ui.screen.search

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.avatars.models.JustHPartyAvatars
import cc.sovellus.vrcaa.api.avatars.providers.JustHPartyProvider
import cc.sovellus.vrcaa.api.models.LimitedWorlds
import cc.sovellus.vrcaa.api.models.Users
import cc.sovellus.vrcaa.helper.api
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    private val context: Context,
    private val query: String
) : StateScreenModel<SearchResultScreenModel.SearchState>(SearchState.Init) {
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
            context.api.get().getWorlds(
                query = query,
                featured = false,
                n = 50,
                sort = "relevance"
            )?.let { foundWorlds = it }

            context.api.get().getUsers(
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