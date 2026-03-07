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

object FavoriteManager : BaseManager<Any>() {

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

    private var worldList = mutableMapOf<String, MutableList<FavoriteMetadata>>()
    private var avatarList = mutableMapOf<String, MutableList<FavoriteMetadata>>()
    private var friendList = mutableMapOf<String, MutableList<FavoriteMetadata>>()

    private val worldListReadonly: Map<String, List<FavoriteMetadata>>
        get() = worldList
    private val avatarListReadonly: Map<String, List<FavoriteMetadata>>
        get() = avatarList
    private val friendListReadonly: Map<String, List<FavoriteMetadata>>
        get() = friendList

    private val versionStateFlow = MutableStateFlow(0L)
    val versionState: StateFlow<Long> = versionStateFlow.asStateFlow()

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

    suspend fun refresh() = coroutineScope {
        favoriteLimits = api.favorites.fetchLimits()

        worldList.clear()
        avatarList.clear()
        friendList.clear()
        tagToGroupMetadataStateFlow.value = emptyMap()

        favoriteLimits?.let {
            repeat(it.maxFavoriteGroups.world) { i ->
                worldList["worlds${i + 1}"] = mutableListOf()
            }
            repeat(it.maxFavoriteGroups.vrcPlusWorld) { i ->
                worldList["vrcPlusWorlds${i + 1}"] = mutableListOf()
            }
            repeat(it.maxFavoriteGroups.avatar) { i ->
                avatarList["avatars${i + 1}"] = mutableListOf()
            }
            repeat(it.maxFavoriteGroups.friend) { i ->
                friendList["group_$i"] = mutableListOf()
            }
        }

        val worldGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_WORLD) + api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_VRC_PLUS_WORLD) }.await()
        val avatarGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_AVATAR) }.await()
        val friendGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_FRIEND) }.await()

        worldGroups.map { group ->
            async {
                val worlds = api.favorites.fetchFavoriteWorlds(group.name)
                val metadataList = worlds.map {
                    FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                }

                worldList[group.name]?.addAll(metadataList)
                putGroupMetadata(
                    group.name,
                    FavoriteGroupMetadata(
                        group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                    )
                )
            }
        }.awaitAll()

        avatarGroups.map { group ->
            async {
                val avatars = api.favorites.fetchFavoriteAvatars(group.name)
                val metadataList = avatars.map {
                    FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                }

                avatarList[group.name]?.addAll(metadataList)
                putGroupMetadata(
                    group.name,
                    FavoriteGroupMetadata(
                        group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                    )
                )
            }
        }.awaitAll()

        friendGroups.map { group ->
            async {
                val friends = api.favorites.fetchFavorites(FavoriteType.FAVORITE_FRIEND, group.name)
                val metadataList = friends.map {
                    FavoriteMetadata(id = it.favoriteId, favoriteId = it.id)
                }

                friendList[group.name]?.addAll(metadataList)
                putGroupMetadata(
                    group.name,
                    FavoriteGroupMetadata(
                        group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                    )
                )
            }
        }.awaitAll()

        versionStateFlow.value += 1
    }

    fun getAvatarList(): Map<String, List<FavoriteMetadata>> {
        return avatarListReadonly
    }

    fun getWorldList(): Map<String, List<FavoriteMetadata>> {
        return worldListReadonly
    }

    fun getFriendList(): Map<String, List<FavoriteMetadata>> {
        return friendListReadonly
    }

    fun getDisplayNameFromTag(tag: String): String {
        val metadata = tagToGroupMetadataStateFlow.value[tag]
        return metadata?.displayName ?: metadata?.name ?: tag
    }

    fun getGroupMetadata(tag: String): FavoriteGroupMetadata? {
        return tagToGroupMetadataStateFlow.value[tag]
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
        if (updated) {
            versionStateFlow.value += 1
        }
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
        if (updated) {
            versionStateFlow.value += 1
        }
        return updated
    }

    // it's the 21th century, and we have computers faster than super computers in our pockets.
    fun isFavorite(type: String, id: String): Boolean {
        return when (type) {
            "world" -> {
                worldList.forEach { group ->
                    group.value.forEach { world ->
                        if (world.id == id)
                            return true
                    }
                }
                false
            }
            "avatar" -> {
                avatarList.forEach { group ->
                    group.value.forEach { avatar ->
                        if (avatar.id == id)
                            return true
                    }
                }
                false
            }
            "friend" -> {
                friendList.forEach { group ->
                    group.value.forEach { friend ->
                        if (friend.id == id)
                            return true
                    }
                }
                false
            }
            else -> false
        }
    }

    private fun getFavoriteId(type: FavoriteType, id: String): Pair<String?, String> {
        return when (type) {
            FavoriteType.FAVORITE_VRC_PLUS_WORLD,
            FavoriteType.FAVORITE_WORLD -> {
                worldList.forEach { group ->
                    group.value.forEach { world ->
                        if (world.id == id)
                            return Pair(world.favoriteId, group.key)
                    }
                }
                Pair(null, "")
            }
            FavoriteType.FAVORITE_AVATAR -> {
                avatarList.forEach { group ->
                    group.value.forEach { avatar ->
                        if (avatar.id == id)
                            return Pair(avatar.favoriteId, group.key)
                    }
                }
                Pair(null, "")
            }
            FavoriteType.FAVORITE_FRIEND -> {
                friendList.forEach { group ->
                    group.value.forEach { friend ->
                        if (friend.id == id)
                            return Pair(friend.favoriteId, group.key)
                    }
                }
                Pair(null, "")
            }
            else -> Pair(null, "")
        }
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
                            worldList[tag]?.add(metadata)
                        }
                    }
                    FavoriteType.FAVORITE_AVATAR -> {
                        metadata?.let {
                            metadata.favoriteId = result.favoriteId
                            avatarList[tag]?.add(metadata)
                        }
                    }
                    FavoriteType.FAVORITE_FRIEND -> {
                        friendList[tag]?.add(FavoriteMetadata(id = id, favoriteId = result.id))
                    }
                    else -> {}
                }
            }

            if (result != null) {
                versionStateFlow.value += 1
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
                            worldList[favorite.second]?.removeIf { it.id == id }
                        }
                        FavoriteType.FAVORITE_AVATAR -> {
                            avatarList[favorite.second]?.removeIf { it.id == id }
                        }
                        FavoriteType.FAVORITE_FRIEND -> {
                            friendList[favorite.second]?.removeIf { it.id == id }
                        }

                        FavoriteType.FAVORITE_NONE -> { }
                        FavoriteType.FAVORITE_VRC_PLUS_WORLD -> { }
                    }

                    updateGroupSize(favorite.second, -1)

                    versionStateFlow.value += 1

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