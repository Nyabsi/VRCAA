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

package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Favorite
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteAdd
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteAvatar
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteGroups
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteLimits
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteWorld

interface IFavorites {

    enum class FavoriteType {
        FAVORITE_NONE,
        FAVORITE_WORLD,
        FAVORITE_AVATAR,
        FAVORITE_FRIEND
    }

    suspend fun fetchLimits(): FavoriteLimits?
    suspend fun fetchFavoriteGroups(type: FavoriteType): FavoriteGroups?
    suspend fun fetchFavoriteGroupsByUserId(userId: String, type: FavoriteType): FavoriteGroups?
    suspend fun addFavorite(type: FavoriteType, favoriteId: String, tag: String): FavoriteAdd?
    suspend fun removeFavorite(favoriteId: String): Boolean
    suspend fun updateFavoriteGroup(type: FavoriteType, tag: String, newDisplayName: String, newVisibility: String?): Boolean
    suspend fun fetchFavorites(type: FavoriteType, tag: String, n: Int = 100, offset: Int = 0, favorites: ArrayList<Favorite> = arrayListOf()): ArrayList<Favorite>
    suspend fun fetchFavoritesByUserId(userId: String, type: FavoriteType, tag: String, n: Int = 100, offset: Int = 0, favorites: ArrayList<Favorite> = arrayListOf()): ArrayList<Favorite>
    suspend fun fetchFavoriteAvatars(tag: String, n: Int = 100, offset: Int = 0, favorites: ArrayList<FavoriteAvatar> = arrayListOf()): ArrayList<FavoriteAvatar>
    suspend fun fetchFavoriteWorlds(tag: String, n: Int = 100, offset: Int = 0, favorites: ArrayList<FavoriteWorld> = arrayListOf()): ArrayList<FavoriteWorld>
}