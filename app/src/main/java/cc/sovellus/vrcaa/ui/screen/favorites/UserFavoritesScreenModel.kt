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
            val worlds: Map<String, List<World>>,
            val avatars: Map<String, List<Avatar>>
        ) : UserFavoriteState()
    }

    private val worldList: MutableMap<String, List<World>> = mutableMapOf()
    private val avatarList: MutableMap<String, List<Avatar>> = mutableMapOf()
    var currentIndex = mutableIntStateOf(0)

    init {
        fetchContent()
    }

    private fun fetchContent() {
        mutableState.value = UserFavoriteState.Loading
        App.setLoadingText(R.string.loading_text_favorites)
        screenModelScope.launch {
            val worldsGroup = (api.favorites.fetchFavoriteGroupsByUserId(userId, FavoriteType.FAVORITE_WORLD)+api.favorites.fetchFavoriteGroupsByUserId(userId, FavoriteType.FAVORITE_VRC_PLUS_WORLD))
            val worldResults = worldsGroup.map { group ->
                async {
                    val worlds = (api.favorites.fetchFavoritesByUserId(userId, FavoriteType.FAVORITE_WORLD, group.name) + api.favorites.fetchFavoritesByUserId(userId, FavoriteType.FAVORITE_VRC_PLUS_WORLD, group.name))

                    val avatar = worlds.map {
                        async {
                            api.worlds.fetchWorldByWorldId(it.favoriteId)
                        }
                    }.awaitAll()

                    group.displayName to avatar
                }
            }.awaitAll()

            worldResults.forEach { (name, list) ->
                worldList[name] = list.filterNotNull()
            }

            val avatarGroups = api.favorites.fetchFavoriteGroupsByUserId(userId, FavoriteType.FAVORITE_AVATAR)
            val avatarResults = avatarGroups.map { group ->
                async {
                    val avatars = api.favorites.fetchFavoritesByUserId(userId, FavoriteType.FAVORITE_AVATAR, group.name)

                    val avatar = avatars.map {
                        async {
                            api.avatars.fetchAvatarById(it.favoriteId)
                        }
                    }.awaitAll()

                    group.displayName to avatar
                }
            }.awaitAll()

            avatarResults.forEach { (name, list) ->
                avatarList[name] = list.filterNotNull()
            }

            mutableState.value = UserFavoriteState.Result(worldList, avatarList)
        }
    }
}