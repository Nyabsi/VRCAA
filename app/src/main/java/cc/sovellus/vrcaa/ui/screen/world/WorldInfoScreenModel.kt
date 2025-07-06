/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.screen.world

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.World
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
        App.setLoadingText(R.string.loading_text_world)
        fetchWorld()
    }

    private fun fetchWorld() {
        screenModelScope.launch {
            val result = api.worlds.fetchWorldByWorldId(id)

            result?.let {
                world = it

                App.setLoadingText(R.string.loading_text_instances)

                val instanceIds = it.instances.map { instance -> instance[0].toString() }
                instanceIds.forEach { id ->
                    api.instances.fetchInstance("${it.id}:${id}").let { instance ->
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
            api.instances.selfInvite(selectedInstanceId.value)
        }
    }

    fun removeFavorite() {
        screenModelScope.launch {
            val result = FavoriteManager.removeFavorite(IFavorites.FavoriteType.FAVORITE_WORLD, world.id)

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