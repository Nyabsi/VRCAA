package cc.sovellus.vrcaa.manager

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteLimits
import cc.sovellus.vrcaa.manager.ApiManager.api

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
        val visibility: String
    )

    private var favoriteLimits: FavoriteLimits? = null

    private var worldList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    private var avatarList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()
    private var friendList = mutableStateMapOf<String, SnapshotStateList<FavoriteMetadata>>()

    private var tagToGroupMetadataMap = mutableStateMapOf<String, FavoriteGroupMetadata>()

    suspend fun refresh()
    {
        favoriteLimits = api.favorites.fetchLimits()

        // populate default lists
        favoriteLimits?.let {
            for (i in 0..<it.maxFavoriteGroups.world)
                worldList["worlds${i+1}"] = SnapshotStateList()

            for (i in 0..<it.maxFavoriteGroups.avatar)
                avatarList["avatars${i+1}"] = SnapshotStateList()

            // This is just ...fucked up.
            for (i in 0..<it.maxFavoriteGroups.friend)
                friendList["group_${i}"] = SnapshotStateList()
        }

        val worldGroups = api.favorites.fetchFavoriteGroups(IFavorites.FavoriteType.FAVORITE_WORLD)

        worldGroups?.forEach { group ->
            val worlds = api.favorites.fetchFavoriteWorlds(group.name)
            worlds.forEach { favorite ->
                worldList[group.name]?.add(FavoriteMetadata(favorite.id, favorite.favoriteId, favorite.name, favorite.thumbnailImageUrl))
            }
            tagToGroupMetadataMap[group.name] = FavoriteGroupMetadata(group.id, group.name, group.type, group.displayName, group.visibility)
        }

        val avatarGroups = api.favorites.fetchFavoriteGroups(IFavorites.FavoriteType.FAVORITE_AVATAR)

        avatarGroups?.forEach { group ->
            val avatars = api.favorites.fetchFavoriteAvatars(group.name)
            avatars.forEach { favorite ->
                avatarList[group.name]?.add(FavoriteMetadata(favorite.id, favorite.favoriteId, favorite.name, favorite.thumbnailImageUrl))
            }
            tagToGroupMetadataMap[group.name] = FavoriteGroupMetadata(group.id, group.name, group.type, group.displayName, group.visibility)
        }

        val friendGroups = api.favorites.fetchFavoriteGroups(IFavorites.FavoriteType.FAVORITE_FRIEND)

        friendGroups?.forEach { group ->
            val friends = api.favorites.fetchFavorites(IFavorites.FavoriteType.FAVORITE_FRIEND, group.name)
            friends.forEach { friend ->
                friendList[group.name]?.add(FavoriteMetadata(id = friend.favoriteId, favoriteId = friend.id))
            }
        }
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
        return tagToGroupMetadataMap[tag]?.displayName ?: tagToGroupMetadataMap[tag]?.name ?: "???"
    }

    fun getGroupMetadata(tag: String): FavoriteGroupMetadata? {
        return tagToGroupMetadataMap[tag]
    }

    suspend fun updateGroupMetadata(tag: String, metadata: FavoriteGroupMetadata): Boolean {
        val tmp = tagToGroupMetadataMap
        tagToGroupMetadataMap[tag] = metadata
        tagToGroupMetadataMap = tmp

        val dType = when (metadata.type) {
            "world" -> FavoriteType.FAVORITE_WORLD
            "avatar" -> FavoriteType.FAVORITE_AVATAR
            "friend" -> FavoriteType.FAVORITE_FRIEND
            else -> FavoriteType.FAVORITE_NONE
        }

        return api.favorites.updateFavoriteGroup(dType, metadata.name, metadata.displayName, metadata.visibility)
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

    private fun getFavoriteId(type: IFavorites.FavoriteType, id: String): Pair<String?, String> {
        return when (type) {
            IFavorites.FavoriteType.FAVORITE_WORLD -> {
                worldList.forEach { group ->
                    group.value.forEach { world ->
                        if (world.id == id)
                            return Pair(world.favoriteId, group.key)
                    }
                }
                Pair(null, "")
            }
            IFavorites.FavoriteType.FAVORITE_AVATAR -> {
                avatarList.forEach { group ->
                    group.value.forEach { avatar ->
                        if (avatar.id == id)
                            return Pair(avatar.favoriteId, group.key)
                    }
                }
                Pair(null, "")
            }
            IFavorites.FavoriteType.FAVORITE_FRIEND -> {
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

    suspend fun addFavorite(type: IFavorites.FavoriteType, id: String, tag: String, metadata: FavoriteMetadata?): Boolean {

        val result = api.favorites.addFavorite(type, id, tag)

        result?.let {
            when (type) {
                IFavorites.FavoriteType.FAVORITE_WORLD -> {
                    metadata?.let {
                        metadata.favoriteId = result.favoriteId
                        worldList[tag]?.add(metadata)
                    }
                }
                IFavorites.FavoriteType.FAVORITE_AVATAR -> {
                    metadata?.let {
                        metadata.favoriteId = result.favoriteId
                        avatarList[tag]?.add(metadata)
                    }
                }
                IFavorites.FavoriteType.FAVORITE_FRIEND -> {
                    friendList[tag]?.add(FavoriteMetadata(id = id, favoriteId = result.id))
                }
                else -> {}
            }
        }

        return result != null
    }

    suspend fun removeFavorite(type: IFavorites.FavoriteType, id: String): Boolean {
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
            }
            return result
        }
        return false
    }
}