package cc.sovellus.vrcaa.ui.screen.world

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.launch

sealed class WorldInfoState {
    data object Init : WorldInfoState()
    data object Loading : WorldInfoState()
    data class Result(
        val world: World,
        val instances: MutableList<Pair<String, Instance>>
    ) : WorldInfoState()
}

class WorldInfoScreenModel(
    private val id: String,
) : StateScreenModel<WorldInfoState>(WorldInfoState.Init) {

    private lateinit var world: World
    private val instances: MutableList<Pair<String, Instance>> = ArrayList()

    var currentTabIndex = mutableIntStateOf(0)
    var selectedInstanceId = mutableStateOf("")

    init {
        mutableState.value = WorldInfoState.Loading
        fetchWorld()
    }

    private fun fetchWorld() {
        screenModelScope.launch {
            App.setLoadingText(R.string.loading_text_world)
            world = api.getWorld(id)

            val instanceIds = world.instances.map { instance ->
                instance[0].toString()
            }

            App.setLoadingText(R.string.loading_text_instances)
            instanceIds.forEach { id ->
                api.getInstance("${world.id}:${id}").let { instance ->
                    instances.add(Pair(id, instance))
                }
            }

            mutableState.value = WorldInfoState.Result(world, instances)
        }
    }

    fun selfInvite() {
        screenModelScope.launch {
            api.inviteSelfToInstance(selectedInstanceId.value)
        }
    }

    fun removeFavorite(callback: (result: Boolean) -> Unit) {
        screenModelScope.launch {
            val result = FavoriteManager.removeFavorite("world", world.id)
            callback(result)
        }
    }
}