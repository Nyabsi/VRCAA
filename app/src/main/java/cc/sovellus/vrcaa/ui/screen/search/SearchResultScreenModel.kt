package cc.sovellus.vrcaa.ui.screen.search

import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Users
import cc.sovellus.vrcaa.api.models.LimitedWorlds
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    private val query: String,
    private val api: ApiContext
) : StateScreenModel<SearchResultScreenModel.SearchState>(SearchState.Init) {

    sealed class SearchState {
        data object Init : SearchState()
        data object Loading : SearchState()
        data class Result(
            val foundWorlds: MutableList<LimitedWorlds.LimitedWorldItem>,
            val foundUsers: MutableList<Users.UsersItem>
        ) : SearchState()
    }

    private var foundWorlds = mutableListOf<LimitedWorlds.LimitedWorldItem>()
    private var foundUsers = mutableListOf<Users.UsersItem>()

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

            mutableState.value = SearchState.Result(foundWorlds, foundUsers)
        }
    }
}