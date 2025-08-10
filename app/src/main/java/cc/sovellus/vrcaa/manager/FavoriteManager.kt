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

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteLimits
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

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
        var size: Int = 0
    )

    private var favoriteLimits: FavoriteLimits? = null

    private var worldList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    private var avatarList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    private var friendList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()

    private var tagToGroupMetadataMap = mutableStateMapOf<String, FavoriteGroupMetadata>()

    suspend fun refresh() = coroutineScope {
        favoriteLimits = api.favorites.fetchLimits()

        favoriteLimits?.let {
            repeat(it.maxFavoriteGroups.world) { i ->
                worldList["worlds${i + 1}"] = SnapshotStateList()
            }
            repeat(it.maxFavoriteGroups.avatar) { i ->
                avatarList["avatars${i + 1}"] = SnapshotStateList()
            }
            repeat(it.maxFavoriteGroups.friend) { i ->
                friendList["group_$i"] = SnapshotStateList()
            }
        }

        val worldGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_WORLD) }.await()
        val avatarGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_AVATAR) }.await()
        val friendGroups = async { api.favorites.fetchFavoriteGroups(FavoriteType.FAVORITE_FRIEND) }.await()

        worldGroups?.map { group ->
            async {
                val worlds = api.favorites.fetchFavoriteWorlds(group.name)
                val metadataList = worlds.map {
                    FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                }

                worldList[group.name]?.addAll(metadataList)
                tagToGroupMetadataMap[group.name] = FavoriteGroupMetadata(
                    group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                )
            }
        }?.awaitAll()

        avatarGroups?.map { group ->
            async {
                val avatars = api.favorites.fetchFavoriteAvatars(group.name)
                val metadataList = avatars.map {
                    FavoriteMetadata(it.id, it.favoriteId, it.name, it.thumbnailImageUrl)
                }

                avatarList[group.name]?.addAll(metadataList)
                tagToGroupMetadataMap[group.name] = FavoriteGroupMetadata(
                    group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                )
            }
        }?.awaitAll()

        friendGroups?.map { group ->
            async {
                val friends = api.favorites.fetchFavorites(FavoriteType.FAVORITE_FRIEND, group.name)
                val metadataList = friends.map {
                    FavoriteMetadata(id = it.favoriteId, favoriteId = it.id)
                }

                friendList[group.name]?.addAll(metadataList)
                tagToGroupMetadataMap[group.name] = FavoriteGroupMetadata(
                    group.id, group.name, group.type, group.displayName, group.visibility, metadataList.size
                )
            }
        }?.awaitAll()
    }

    fun getAvatarList(): SnapshotStateMap<String, SnapshotStateList<FavoriteMetadata>> {
        return avatarList
    }

    fun getWorldList(): SnapshotStateMap<String, SnapshotStateList<FavoriteMetadata>> {
        return worldList
    }

    fun getFriendList(): SnapshotStateMap<String, SnapshotStateList<FavoriteMetadata>> {
        return friendList
    }

    fun getDisplayNameFromTag(tag: String): String {
        return tagToGroupMetadataMap[tag]?.displayName ?: tagToGroupMetadataMap[tag]?.name ?: tag
    }

    fun getGroupMetadata(tag: String): FavoriteGroupMetadata? {
        return tagToGroupMetadataMap[tag]
    }

    suspend fun updateGroupMetadata(tag: String, metadata: FavoriteGroupMetadata): Boolean {

        val tmp = tagToGroupMetadataMap
        tagToGroupMetadataMap[tag] = metadata
        tagToGroupMetadataMap[tag]?.size = tmp[tag]?.size ?: -1 // re-adjust size

        val dType = when (metadata.type) {
            "world" -> FavoriteType.FAVORITE_WORLD
            "avatar" -> FavoriteType.FAVORITE_AVATAR
            "friend" -> FavoriteType.FAVORITE_FRIEND
            else -> FavoriteType.FAVORITE_NONE
        }

        return api.favorites.updateFavoriteGroup(dType, metadata.name, metadata.displayName, metadata.visibility)
    }

    suspend fun updateGroupMetadataOnlyName(tag: String, metadata: FavoriteGroupMetadata): Boolean {

        val tmp = tagToGroupMetadataMap
        tagToGroupMetadataMap[tag] = metadata
        tagToGroupMetadataMap[tag]?.size = tmp[tag]?.size ?: -1 // re-adjust size

        val dType = when (metadata.type) {
            "world" -> FavoriteType.FAVORITE_WORLD
            "avatar" -> FavoriteType.FAVORITE_AVATAR
            "friend" -> FavoriteType.FAVORITE_FRIEND
            else -> FavoriteType.FAVORITE_NONE
        }

        return api.favorites.updateFavoriteGroup(dType, metadata.name, metadata.displayName, null)
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

            tagToGroupMetadataMap[tag]?.let { groupMetadata ->
                groupMetadata.size += 1
            }

            result?.let {
                when (type) {
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
                    }

                    tagToGroupMetadataMap[favorite.second]?.let { groupMetadata ->
                        groupMetadata.size -= 1
                    }

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
                FavoriteType.FAVORITE_AVATAR -> limits.maxFavoritesPerGroup.avatar
                FavoriteType.FAVORITE_FRIEND -> limits.maxFavoritesPerGroup.friend
                FavoriteType.FAVORITE_NONE -> 0
            }
        }
        return -1
    }
}