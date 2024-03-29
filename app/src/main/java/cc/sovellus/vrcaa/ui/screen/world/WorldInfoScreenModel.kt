package cc.sovellus.vrcaa.ui.screen.world

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.models.Instance
import cc.sovellus.vrcaa.api.http.models.Instances
import cc.sovellus.vrcaa.api.http.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class WorldInfoScreenModel(
    private val context: Context,
    private val id: String
) : StateScreenModel<WorldInfoScreenModel.WorldInfoState>(WorldInfoState.Init) {

    sealed class WorldInfoState {
        data object Init : WorldInfoState()
        data object Loading : WorldInfoState()
        data class Result(val world: World?, val instances: MutableList<Pair<String, Instance>>) : WorldInfoState()
    }

    private var world: World? = null
    private val instances: MutableList<Pair<String, Instance>> = ArrayList()

    var currentIndex = mutableIntStateOf(0)
    var clickedInstance = mutableStateOf("")

    init {
        mutableState.value = WorldInfoState.Loading
        getProfile()
    }

    private fun getProfile() {
        screenModelScope.launch {
            world = api.getWorld(id)

            val instanceMap = world?.instances?.map { Instances(it[0].toString(), it[1].toString().toDouble()) }
            if (instanceMap != null) {
                for (instance in instanceMap) {
                    api.getInstance("${world?.id}:${instance.intent}").let {
                        if (it == null) {
                            Toast.makeText(
                                context,
                                "Failed to fetch instance due to API error, try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            instances.add(Pair(instance.intent, it))
                        }
                    }
                }
            }
            mutableState.value = WorldInfoState.Result(world, instances)
        }
    }

    fun selfInvite() {
        screenModelScope.launch {
            api.InviteSelfToInstance(clickedInstance.value)
        }
    }
}