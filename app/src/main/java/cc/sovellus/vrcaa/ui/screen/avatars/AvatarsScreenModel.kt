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

package cc.sovellus.vrcaa.ui.screen.avatars

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class AvatarsScreenModel : StateScreenModel<AvatarsScreenModel.AvatarsState>(AvatarsState.Init) {

    sealed class AvatarsState {
        data object Init : AvatarsState()
        data object Loading : AvatarsState()
        data object Empty : AvatarsState()
        data class Result(
            val avatars: ArrayList<Avatar>
        ) : AvatarsState()
    }

    private var avatars: ArrayList<Avatar> = arrayListOf()

    init {
        mutableState.value = AvatarsState.Loading
        App.setLoadingText(R.string.loading_text_avatars)
        fetchAvatars()
    }

    private fun fetchAvatars() {
        screenModelScope.launch {
            avatars = api.user.fetchOwnedAvatars()

            if (avatars.isEmpty())
                mutableState.value = AvatarsState.Empty
            else
                mutableState.value = AvatarsState.Result(avatars)
        }
    }
}