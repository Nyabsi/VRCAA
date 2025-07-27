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
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
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
            val worlds: MutableMap<String, SnapshotStateList<World?>>,
            val avatars: MutableMap<String, SnapshotStateList<Avatar?>>
        ) : UserFavoriteState()
    }

    var worldList: MutableMap<String, SnapshotStateList<World?>> = mutableStateMapOf()
    var avatarList: MutableMap<String, SnapshotStateList<Avatar?>> = mutableStateMapOf()
    var currentIndex = mutableIntStateOf(0)

    init {
        fetchContent()
    }

    private fun fetchContent() {
        mutableState.value = UserFavoriteState.Loading
        App.setLoadingText(R.string.loading_text_favorites)
        screenModelScope.launch {
            val worldsGroup = api.favorites.fetchFavoriteGroupsByUserId(userId, FavoriteType.FAVORITE_WORLD)
            val worldResults = worldsGroup?.map { group ->
                async {
                    val worlds = api.favorites.fetchFavoritesByUserId(userId, FavoriteType.FAVORITE_WORLD, group.name)

                    val avatar = worlds.map {
                        async {
                            api.worlds.fetchWorldByWorldId(it.favoriteId)
                        }
                    }.awaitAll()

                    group.displayName to avatar
                }
            }?.awaitAll()

            worldResults?.forEach { (name, list) ->
                worldList[name] = list.toMutableStateList()
            }

            val avatarGroups = api.favorites.fetchFavoriteGroupsByUserId(userId, FavoriteType.FAVORITE_AVATAR)
            val avatarResults = avatarGroups?.map { group ->
                async {
                    val avatars = api.favorites.fetchFavoritesByUserId(userId, FavoriteType.FAVORITE_AVATAR, group.name)

                    val avatar = avatars.map {
                        async {
                            api.avatars.fetchAvatarById(it.favoriteId)
                        }
                    }.awaitAll()

                    group.displayName to avatar
                }
            }?.awaitAll()

            avatarResults?.forEach { (name, list) ->
                avatarList[name] = list.toMutableStateList()
            }

            mutableState.value = UserFavoriteState.Result(worldList, avatarList)
        }
    }
}