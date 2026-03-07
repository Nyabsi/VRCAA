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

package cc.sovellus.vrcaa.ui.screen.favorites

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesScreenModel : StateScreenModel<FavoritesScreenModel.FavoriteState>(FavoriteState.Init) {

    sealed class FavoriteState {
        data object Init : FavoriteState()
        data object Loading : FavoriteState()
        data object Result : FavoriteState()
    }

    val version: StateFlow<Long> = FavoriteManager.versionState
    val groupMetadata: StateFlow<Map<String, FavoriteManager.FavoriteGroupMetadata>> = FavoriteManager.groupMetadataState

    var currentIndex = mutableIntStateOf(0)
    var currentSelectedGroup = mutableStateOf("")
    var editDialogShown = mutableStateOf(false)
    var currentSelectedIsFriend = mutableStateOf(false)
    var currentSelectedType = mutableStateOf(IFavorites.FavoriteType.FAVORITE_NONE)
    var currentSelectedId = mutableStateOf("")
    var deleteDialogShown = mutableStateOf(false)

    private val cacheListener = object : CacheManager.CacheListener {
        override fun startCacheRefresh() {
            mutableState.value = FavoriteState.Loading
        }

        override fun endCacheRefresh() {
            mutableState.value = FavoriteState.Result
        }
    }

    init {
        mutableState.value = FavoriteState.Loading
        CacheManager.addListener(cacheListener)

        if (CacheManager.isBuilt())
        {
            mutableState.value = FavoriteState.Result
        } else {
            mutableState.value = FavoriteState.Loading
        }
    }

    fun getWorldList(): Map<String, List<FavoriteManager.FavoriteMetadata>> {
        return FavoriteManager.getWorldList()
    }

    fun getAvatarList(): Map<String, List<FavoriteManager.FavoriteMetadata>> {
        return FavoriteManager.getAvatarList()
    }

    fun getFriendList(): Map<String, List<FavoriteManager.FavoriteMetadata>> {
        return FavoriteManager.getFriendList()
    }

    fun removeFavorite() {
        screenModelScope.launch {
            mutableState.value = FavoriteState.Loading
            FavoriteManager.removeFavorite(currentSelectedType.value, currentSelectedId.value)
            mutableState.value = FavoriteState.Result
        }
    }

    override fun onDispose() {
        CacheManager.removeListener(cacheListener)
        super.onDispose()
    }
}