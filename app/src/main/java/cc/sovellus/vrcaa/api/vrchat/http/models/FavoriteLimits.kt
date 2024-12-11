package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class FavoriteLimits(
    @SerializedName("defaultMaxFavoriteGroups")
    var defaultMaxFavoriteGroups: Int = 0,
    @SerializedName("defaultMaxFavoritesPerGroup")
    var defaultMaxFavoritesPerGroup: Int = 0,
    @SerializedName("maxFavoriteGroups")
    var maxFavoriteGroups: MaxFavoriteGroups = MaxFavoriteGroups(),
    @SerializedName("maxFavoritesPerGroup")
    var maxFavoritesPerGroup: MaxFavoritesPerGroup = MaxFavoritesPerGroup()
)