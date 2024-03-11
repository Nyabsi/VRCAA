package cc.sovellus.vrcaa.ui.screen.world

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.models.World
import cc.sovellus.vrcaa.helper.api
import kotlinx.coroutines.launch

class WorldInfoScreenModel(
    private val context: Context,
    private val id: String
) : StateScreenModel<WorldInfoScreenModel.WorldInfoState>(WorldInfoState.Init) {

    sealed class WorldInfoState {
        data object Init : WorldInfoState()
        data object Loading : WorldInfoState()
        data class Result(val world: World) : WorldInfoState()
    }

    private var world = mutableStateOf<World?>(null)

    init {
        mutableState.value = WorldInfoState.Loading
        getProfile()
    }

    private fun getProfile() {
        screenModelScope.launch {
            world.value = context.api.get().getWorld(id)
            mutableState.value = WorldInfoState.Result(world.value!!)
        }
    }
}