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

class WorldInfoScreenModel(
    private val id: String,
) : StateScreenModel<WorldInfoScreenModel.WorldInfoState>(WorldInfoState.Init) {

    private val context: Context = App.getContext()

    sealed class WorldInfoState {
        data object Init : WorldInfoState()
        data object Loading : WorldInfoState()
        data object Failure : WorldInfoState()
        data class Result(
            val world: World,
            val instances: MutableList<Pair<String, Instance?>>
        ) : WorldInfoState()
    }

    private lateinit var world: World
    private val instances: MutableList<Pair<String, Instance?>> = ArrayList()

    var currentTabIndex = mutableIntStateOf(0)
    var selectedInstanceId = mutableStateOf("")

    init {
        mutableState.value = WorldInfoState.Loading
        fetchWorld()
    }

    private fun fetchWorld() {
        screenModelScope.launch {
            App.setLoadingText(R.string.loading_text_world)
            val result = api.getWorld(id)

            result?.let {
                world = it

                App.setLoadingText(R.string.loading_text_instances)

                val instanceIds = it.instances.map { instance -> instance[0].toString() }
                instanceIds.forEach { id ->
                    api.getInstance("${it.id}:${id}").let { instance ->
                        instances.add(Pair(id, instance))
                    }
                }

                mutableState.value = WorldInfoState.Result(it, instances)
            } ?: run {
                mutableState.value = WorldInfoState.Failure
            }
        }
    }

    fun selfInvite() {
        screenModelScope.launch {
            api.inviteSelfToInstance(selectedInstanceId.value)
        }
    }

    fun removeFavorite() {
        screenModelScope.launch {
            val result = FavoriteManager.removeFavorite("world", world.id)

            if (result) {
                Toast.makeText(
                    context,
                    context.getString(R.string.favorite_toast_favorite_removed)
                        .format(world.name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.favorite_toast_favorite_removed_failed)
                        .format(world.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}