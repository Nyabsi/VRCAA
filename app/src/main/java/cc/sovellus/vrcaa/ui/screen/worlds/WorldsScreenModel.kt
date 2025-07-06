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

package cc.sovellus.vrcaa.ui.screen.worlds

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreenModel.WorldsState
import kotlinx.coroutines.launch

class WorldsScreenModel(
    private val userId: String,
    private val private: Boolean
) : StateScreenModel<WorldsState>(WorldsState.Init) {

    sealed class WorldsState {
        data object Init : WorldsState()
        data object Loading : WorldsState()
        data class Result(
            val worlds: ArrayList<World>
        ) : WorldsState()
    }

    private var worlds: ArrayList<World> = arrayListOf()

    init {
        mutableState.value = WorldsState.Loading
        App.setLoadingText(R.string.loading_text_worlds)
        fetchAvatars()
    }

    private fun fetchAvatars() {
        screenModelScope.launch {
            worlds = api.worlds.fetchWorldsByAuthorId(userId, private)
            mutableState.value = WorldsState.Result(worlds)
        }
    }
}