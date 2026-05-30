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

package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteLimits
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FavoriteManager {

    data class FavoriteMetadata(
        val id: String,
        var favoriteId: String,
        val name: String = "",
        val thumbnailUrl: String = ""
    )

    data class FavoriteGroupMetadata(
        val id: String,
        val name: String,
        val type: String,
        val displayName: String,
        val visibility: String,
        val size: Int = 0
    )

    private var favoriteLimits: FavoriteLimits? = null

    private val worldListStateFlow = MutableStateFlow<Map<String, List<FavoriteMetadata>>>(emptyMap())
    private val avatarListStateFlow = MutableStateFlow<Map<String, List<FavoriteMetadata>>>(emptyMap())
    private val friendListStateFlow = MutableStateFlow<Map<String, List<FavoriteMetadata>>>(emptyMap())

    val worldListState: StateFlow<Map<String, List<FavoriteMetadata>>> = worldListStateFlow.asStateFlow()
    val avatarListState: StateFlow<Map<String, List<FavoriteMetadata>>> = avatarListStateFlow.asStateFlow()
    val friendListState: StateFlow<Map<String, List<FavoriteMetadata>>> = friendListStateFlow.asStateFlow()

    private val tagToGroupMetadataStateFlow = MutableStateFlow<Map<String, FavoriteGroupMetadata>>(emptyMap())
    val groupMetadataState: StateFlow<Map<String, FavoriteGroupMetadata>> = tagToGroupMetadataStateFlow.asStateFlow()

    private fun putGroupMetadata(tag: String, metadata: FavoriteGroupMetadata) {
        tagToGroupMetadataStateFlow.update { current ->
            current + (tag to metadata)
        }
    }

    private fun putGroupMetadataPreservingSize(tag: String, metadata: FavoriteGroupMetadata) {
        tagToGroupMetadataStateFlow.update { current ->
            val previousSize = current[tag]?.size ?: -1
            current + (tag to metadata.copy(size = previousSize))
        }
    }

    private fun updateGroupSize(tag: String, delta: Int) {
        tagToGroupMetadataStateFlow.update { current ->
            val existing = current[tag] ?: return@update current
            current + (tag to existing.copy(size = existing.size + delta))
        }
    }

    private fun appendToGroup(
        flow: MutableStateFlow<Map<String, List<FavoriteMetadata>>>,
        tag: String,
        items: List<FavoriteMetadata>
    ) {
        flow.update { current ->
            val existing = current[tag] ?: return@update current
            current + (tag to (existing + items))
        }
    }

    private fun addToGroup(
        flow: MutableStateFlow<Map<String, List<FavoriteMetadata>>>,
        tag: String,
        item: FavoriteMetadata
    ) {
        flow.update { current ->
            val existing = current[tag] ?: return@update current
            current + (tag to (existing + item))
        }
    }

    private fun removeFromGroup(
        flow: MutableStateFlow<Map<String, List<FavoriteMetadata>>>,
        tag: String,
        id: String
    ) {
        flow.update { current ->
            val existing = current[tag] ?: return@update current
            current + (tag to existing.filterNot { it.id == id })
        }
    }

    suspend fun refresh() = coroutineScope {
        favoriteLimits = api.favorites.fetchLimits()

        tagToGroupMetadataStateFlow.value = emptyMap()

        val initialWorlds = mutableMapOf<String, List<FavoriteMetadata>>()
        val initialAvatars = mutableMapOf<String, List<FavoriteMetadata>>()
        val initialFriends = mutableMapOf<String, List<FavoriteMetadata>>()

        favoriteLimits?.let {
            repeat(it.maxFavoriteGroups.world) { i ->
                initialWorlds["worlds${i + 1}"] = emptyList()
            }
            repeat(it.maxFavoriteGroups.vrcPlusWorld) { i ->
                initialWorlds["vrcPlusWorlds${i + 1}"] = emptyList()
            }
            repeat(it.maxFavoriteGroups.avatar) { i ->
                initialAvatars["avatars${i + 1}"] = emptyList()
            }
            repeat(it.maxFavoriteGroups.friend) { i ->
                initialFriends["group_$i"] = emptyList()
            }
        }

        worldListStateFlow.value = initialWorlds
        avatarListStateFlow.value = initialAvatars
        friendListStateFlow.value = initialFriends

        val worldGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_WORLD) + api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_VRC_PLUS_WORLD) }
        val avatarGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_AVATAR) }
        val friendGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_FRIEND) }

        val worldJob = async {
            worldGroups.await().map { group ->
                async {
                    val worlds = api.favorites.fetchFavoriteWorlds(group.name)
                    val metadataList = worlds.map {
                        FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                    }
                    appendToGroup(worldListStateFlow, group.name, metadataList)
                    putGroupMetadata(
                        group.name,
                        FavoriteGroupMetadata(
                            group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                        )
                    )
                }
            }.awaitAll()
        }

        val avatarJob = async {
            avatarGroups.await().map { group ->
                async {
                    val avatars = api.favorites.fetchFavoriteAvatars(group.name)
                    val metadataList = avatars.map {
                        FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                    }
                    appendToGroup(avatarListStateFlow, group.name, metadataList)
                    putGroupMetadata(
                        group.name,
                        FavoriteGroupMetadata(
                            group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                        )
                    )
                }
            }.awaitAll()
        }

        val friendJob = async {
            friendGroups.await().map { group ->
                async {
                    val friends = api.favorites.fetchFavorites(FavoriteType.FAVORITE_FRIEND, group.name)
                    val metadataList = friends.map {
                        FavoriteMetadata(id = it.favoriteId, favoriteId = it.id)
                    }
                    appendToGroup(friendListStateFlow, group.name, metadataList)
                    putGroupMetadata(
                        group.name,
                        FavoriteGroupMetadata(
                            group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                        )
                    )
                }
            }.awaitAll()
        }

        awaitAll(worldJob, avatarJob, friendJob)
    }

    suspend fun updateGroupMetadata(tag: String, metadata: FavoriteGroupMetadata): Boolean {
        putGroupMetadataPreservingSize(tag, metadata)

        val dType = when (metadata.type) {
            "world" -> FavoriteType.FAVORITE_WORLD
            "vrcPlusWorld" -> FavoriteType.FAVORITE_VRC_PLUS_WORLD
            "avatar" -> FavoriteType.FAVORITE_AVATAR
            "friend" -> FavoriteType.FAVORITE_FRIEND
            else -> FavoriteType.FAVORITE_NONE
        }

        val updated = api.favorites.updateFavoriteGroup(dType, metadata.name, metadata.displayName, metadata.visibility)
        return updated
    }

    suspend fun updateGroupMetadataOnlyName(tag: String, metadata: FavoriteGroupMetadata): Boolean {
        putGroupMetadataPreservingSize(tag, metadata)

        val dType = when (metadata.type) {
            "world" -> FavoriteType.FAVORITE_WORLD
            "vrcPlusWorld" -> FavoriteType.FAVORITE_VRC_PLUS_WORLD
            "avatar" -> FavoriteType.FAVORITE_AVATAR
            "friend" -> FavoriteType.FAVORITE_FRIEND
            else -> FavoriteType.FAVORITE_NONE
        }

        val updated = api.favorites.updateFavoriteGroup(dType, metadata.name, metadata.displayName, null)
        return updated
    }

    // it's the 21th century, and we have computers faster than super computers in our pockets.
    fun isFavorite(type: String, id: String): Boolean {
        return when (type) {
            "world" -> worldListStateFlow.value.values.any { group -> group.any { it.id == id } }
            "avatar" -> avatarListStateFlow.value.values.any { group -> group.any { it.id == id } }
            "friend" -> friendListStateFlow.value.values.any { group -> group.any { it.id == id } }
            else -> false
        }
    }

    private fun getFavoriteId(type: FavoriteType, id: String): Pair<String?, String> {
        val source = when (type) {
            FavoriteType.FAVORITE_VRC_PLUS_WORLD,
            FavoriteType.FAVORITE_WORLD -> worldListStateFlow.value
            FavoriteType.FAVORITE_AVATAR -> avatarListStateFlow.value
            FavoriteType.FAVORITE_FRIEND -> friendListStateFlow.value
            else -> return Pair(null, "")
        }

        source.forEach { (tag, group) ->
            group.forEach { item ->
                if (item.id == id)
                    return Pair(item.favoriteId, tag)
            }
        }
        return Pair(null, "")
    }

    suspend fun addFavorite(type: FavoriteType, id: String, tag: String, metadata: FavoriteMetadata?): Boolean {
        try {
            val result = api.favorites.addFavorite(type, id, tag)

            updateGroupSize(tag, 1)

            result?.let {
                when (type) {
                    FavoriteType.FAVORITE_VRC_PLUS_WORLD,
                    FavoriteType.FAVORITE_WORLD -> {
                        metadata?.let {
                            metadata.favoriteId = result.favoriteId
                            addToGroup(worldListStateFlow, tag, metadata)
                        }
                    }
                    FavoriteType.FAVORITE_AVATAR -> {
                        metadata?.let {
                            metadata.favoriteId = result.favoriteId
                            addToGroup(avatarListStateFlow, tag, metadata)
                        }
                    }
                    FavoriteType.FAVORITE_FRIEND -> {
                        addToGroup(friendListStateFlow, tag, FavoriteMetadata(id = id, favoriteId = result.id))
                    }
                    else -> {}
                }
            }

            return result != null
        } catch (_: Throwable) {
            return false
        }
    }

    suspend fun removeFavorite(type: FavoriteType, id: String): Boolean {
        try {
            val favorite = getFavoriteId(type, id)
            favorite.first?.let { favoriteId ->
                val result = api.favorites.removeFavorite(favoriteId)
                if (result)
                {
                    when (type) {
                        FavoriteType.FAVORITE_WORLD -> {
                            removeFromGroup(worldListStateFlow, favorite.second, id)
                        }
                        FavoriteType.FAVORITE_AVATAR -> {
                            removeFromGroup(avatarListStateFlow, favorite.second, id)
                        }
                        FavoriteType.FAVORITE_FRIEND -> {
                            removeFromGroup(friendListStateFlow, favorite.second, id)
                        }

                        FavoriteType.FAVORITE_NONE -> { }
                        FavoriteType.FAVORITE_VRC_PLUS_WORLD -> { }
                    }

                    updateGroupSize(favorite.second, -1)
                }
                return result
            }
            return false
        } catch (_: Throwable) {
            return false
        }
    }

    fun getMaximumFavoritesForType(type: FavoriteType): Int {
        favoriteLimits?.let { limits ->
            return when (type) {
                FavoriteType.FAVORITE_WORLD -> limits.maxFavoritesPerGroup.world
                FavoriteType.FAVORITE_VRC_PLUS_WORLD -> limits.maxFavoritesPerGroup.vrcPlusWorld
                FavoriteType.FAVORITE_AVATAR -> limits.maxFavoritesPerGroup.avatar
                FavoriteType.FAVORITE_FRIEND -> limits.maxFavoritesPerGroup.friend
                FavoriteType.FAVORITE_NONE -> 0
            }
        }
        return -1
    }
}