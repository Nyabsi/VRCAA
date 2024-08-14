package cc.sovellus.vrcaa.manager

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import cc.sovellus.vrcaa.manager.ApiManager.api

object FavoriteManager {

    data class FavoriteMetadata(
        val id: String,
        var favoriteId: String,
        val name: String,
        val thumbnailUrl: String
    )

    private var worldList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    private var avatarList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    private var tagToDisplayNameMap = mutableStateMapOf<String, String>()

    suspend fun init()
    {
        val limits = api.getFavoriteLimits()

        // populate default lists
        limits?.let {
            for (i in 0..<it.maxFavoriteGroups.world)
                worldList["worlds${i+1}"] = SnapshotStateList()

            for (i in 0..<it.maxFavoriteGroups.avatar)
                avatarList["avatars${i+1}"] = SnapshotStateList()
        }

        val worldGroups = api.getFavoriteGroups("world")

        worldGroups?.forEach { group ->
            val worlds = api.getFavoriteWorlds(group.name)
            worlds.forEach { favorite ->
                worldList[group.name]?.add(FavoriteMetadata(favorite.id, favorite.favoriteId, favorite.name, favorite.thumbnailImageUrl))
            }
            tagToDisplayNameMap[group.name] = group.displayName
        }

        val avatarGroups = api.getFavoriteGroups("avatar")

        avatarGroups?.forEach { group ->
            val avatars = api.getFavoriteAvatars(group.name)
            avatars.forEach { favorite ->
                avatarList[group.name]?.add(FavoriteMetadata(favorite.id, favorite.favoriteId, favorite.name, favorite.thumbnailImageUrl))
            }
            tagToDisplayNameMap[group.name] = group.displayName
        }
    }

    fun getAvatarList(): SnapshotStateMap<String, SnapshotStateList<FavoriteMetadata>> {
        return avatarList
    }

    fun getWorldList(): SnapshotStateMap<String, SnapshotStateList<FavoriteMetadata>> {
        return worldList
    }

    fun getDisplayNameFromTag(tag: String): String? {
        return tagToDisplayNameMap[tag]
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
            else -> false
        }
    }

    private fun getFavoriteId(type: String, id: String): Pair<String?, String> {
        return when (type) {
            "world" -> {
                worldList.forEach { group ->
                    group.value.forEach { world ->
                        if (world.id == id)
                            return Pair(world.favoriteId, group.key)
                    }
                }
                Pair(null, "")
            }
            "avatar" -> {
                avatarList.forEach { group ->
                    group.value.forEach { avatar ->
                        if (avatar.id == id)
                            return Pair(avatar.favoriteId, group.key)
                    }
                }
                Pair(null, "")
            }
            else -> Pair(null, "")
        }
    }

    suspend fun addFavorite(type: String, id: String, tag: String, metadata: FavoriteMetadata): Boolean {

        val result = api.addFavorite(type, id, tag)

        result?.let {
            when (type) {
                "world" -> {
                    metadata.favoriteId = result.favoriteId
                    worldList[tag]?.add(metadata)
                }
                "avatar" -> {
                    metadata.favoriteId = result.favoriteId
                    avatarList[tag]?.add(metadata)
                }
                else -> {}
            }
        }

        return result != null
    }

    suspend fun removeFavorite(type: String, id: String): Boolean {
        val favorite = getFavoriteId(type, id)
        favorite.first?.let { favoriteId ->
            val result = api.removeFavorite(favoriteId)
            if (result) {
                when (type) {
                    "world" -> {
                        worldList[favorite.second]?.removeIf { it.id == id }
                    }
                    "avatar" -> {
                        avatarList[favorite.second]?.removeIf { it.id == id }
                    }
                    else -> {}
                }
            }
            return result
        }
        return false
    }
}