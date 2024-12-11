package cc.sovellus.vrcaa.ui.screen.worlds

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

sealed class WorldsState {
    data object Init : WorldsState()
    data object Loading : WorldsState()
    data class Result(
        val worlds: ArrayList<World>
    ) : WorldsState()
}

class WorldsStateScreenModel(
    private val userId: String,
    private val private: Boolean
) : StateScreenModel<WorldsState>(WorldsState.Init) {

    private var worlds: ArrayList<World> = arrayListOf()

    init {
        mutableState.value = WorldsState.Loading
        fetchAvatars()
    }

    private fun fetchAvatars() {
        screenModelScope.launch {
            worlds = api.worlds.fetchWorldsByAuthorId(userId, private)
            mutableState.value = WorldsState.Result(worlds)
        }
    }
}