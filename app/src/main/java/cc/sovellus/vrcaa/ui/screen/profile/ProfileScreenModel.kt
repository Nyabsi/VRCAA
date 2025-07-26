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

package cc.sovellus.vrcaa.ui.screen.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.manager.CacheManager

class ProfileScreenModel : StateScreenModel<ProfileScreenModel.ProfileState>(ProfileState.Init) {

    sealed class ProfileState {
        data object Init : ProfileState()
        data object Loading : ProfileState()
        data class Result(val profile: User) : ProfileState()
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun profileUpdated(profile: User) {
            mutableState.value = ProfileState.Loading
            mutableState.value = ProfileState.Result(profile)
        }

        override fun startCacheRefresh() {
            mutableState.value = ProfileState.Loading
        }

        override fun endCacheRefresh() {
            fetchProfile()
        }
    }

    init {
        mutableState.value = ProfileState.Loading
        CacheManager.addListener(cacheListener)

        if (CacheManager.isBuilt())
            fetchProfile()
    }

    private fun fetchProfile() {
        val profile = CacheManager.getProfile()
        if (profile == null)
            fetchProfile()
        else
            mutableState.value = ProfileState.Result(profile)
    }
}