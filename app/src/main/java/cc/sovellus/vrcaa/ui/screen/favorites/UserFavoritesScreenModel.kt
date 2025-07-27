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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager.FavoriteMetadata
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class UserFavoritesScreenModel(
    private val userId: String
) : StateScreenModel<UserFavoritesScreenModel.UserFavoriteState>(UserFavoriteState.Init) {

    sealed class UserFavoriteState {
        data object Init : UserFavoriteState()
        data object Loading : UserFavoriteState()
        data class Result(
            val worlds: SnapshotStateMap<String, SnapshotStateList<FavoriteMetadata>>,
            val avatars: SnapshotStateMap<String, SnapshotStateList<FavoriteMetadata>>
        ) : UserFavoriteState()
    }

    var worldList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    var avatarList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = UserFavoriteState.Init
        fetchContent()
    }

    private fun fetchContent() {
        mutableState.value = UserFavoriteState.Loading
        screenModelScope.launch {
            val worldsGroup = async { api.favorites.fetchFavoriteGroupsByUserId(userId, FavoriteType.FAVORITE_WORLD) }.await()

            worldsGroup?.map { group ->
                async {
                    val worlds = api.favorites.fetchFavoriteWorldsByUserId(userId, group.name)
                    val metadataList = worlds.map {
                        FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                    }

                    worldList[group.name] = metadataList.toMutableStateList()
                }
            }?.awaitAll()

            val avatarsGroup = async { api.favorites.fetchFavoriteGroupsByUserId(userId, FavoriteType.FAVORITE_AVATAR) }.await()

            avatarsGroup?.map { group ->
                async {
                    val worlds = api.favorites.fetchFavoriteAvatarsByUserId(userId, group.name)
                    val metadataList = worlds.map {
                        FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                    }

                    avatarList[group.name] = metadataList.toMutableStateList()
                }
            }?.awaitAll()

            mutableState.value = UserFavoriteState.Result(worldList, avatarList)
        }
    }
}