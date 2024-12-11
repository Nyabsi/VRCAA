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
    suspend fun addFavorite(type: FavoriteType, favoriteId: String, tag: String): FavoriteAdd?
    suspend fun removeFavorite(favoriteId: String): Boolean
    suspend fun updateFavoriteGroup(type: FavoriteType, tag: String, newDisplayName: String, newVisibility: String): Boolean
    suspend fun fetchFavorites(type: FavoriteType, tag: String, n: Int = 50, offset: Int = 0, favorites: ArrayList<Favorite> = arrayListOf()): ArrayList<Favorite>
    suspend fun fetchFavoriteAvatars(tag: String, n: Int = 50, offset: Int = 0, favorites: ArrayList<FavoriteAvatar> = arrayListOf()): ArrayList<FavoriteAvatar>
    suspend fun fetchFavoriteWorlds(tag: String, n: Int = 50, offset: Int = 0, favorites: ArrayList<FavoriteWorld> = arrayListOf()): ArrayList<FavoriteWorld>
}