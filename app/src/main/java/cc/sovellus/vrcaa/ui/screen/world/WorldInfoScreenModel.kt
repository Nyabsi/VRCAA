package cc.sovellus.vrcaa.ui.screen.world

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.World
import kotlinx.coroutines.launch

class WorldInfoScreenModel(
    context: Context,
    private val id: String
) : StateScreenModel<WorldInfoScreenModel.WorldInfoState>(WorldInfoState.Init) {
    private val api = ApiContext(context)

    sealed class WorldInfoState {
        data object Init : WorldInfoState()
        data object Loading : WorldInfoState()
        data class Result (val world: World) : WorldInfoState()
    }

    private var world = mutableStateOf<World?>(null)

    init {
        mutableState.value = WorldInfoState.Loading
        getProfile()
    }
    private fun getProfile() {
        screenModelScope.launch {
            world.value = api.getWorld(id)
            mutableState.value = WorldInfoState.Result(world.value!!)
        }
    }
}