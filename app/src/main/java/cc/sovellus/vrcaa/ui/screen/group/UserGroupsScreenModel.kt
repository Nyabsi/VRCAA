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

package cc.sovellus.vrcaa.ui.screen.group

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.UserGroup
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class UserGroupsScreenModel(
    private val userId: String
) : StateScreenModel<UserGroupsScreenModel.UserGroupsState>(UserGroupsState.Init) {

    sealed class UserGroupsState {
        data object Init : UserGroupsState()
        data object Loading : UserGroupsState()
        data object Empty : UserGroupsState()
        data class Result(val groups: List<UserGroup>) : UserGroupsState()
    }

    init {
        mutableState.value = UserGroupsState.Loading
        App.setLoadingText(R.string.loading_text_groups)
        fetchGroups()
    }

    private fun fetchGroups() {
        screenModelScope.launch {
            val result = api.users.fetchGroupsByUserId(userId).sortedBy { it.ownerId != userId }
            if (result.isEmpty())
                mutableState.value = UserGroupsState.Empty
            else
                mutableState.value = UserGroupsState.Result(result)
        }
    }
}