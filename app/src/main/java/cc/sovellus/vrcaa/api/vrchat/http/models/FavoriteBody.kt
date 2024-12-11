package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class FavoriteBody(
    @SerializedName("type")
    var type: String = "",
    @SerializedName("favoriteId")
    var favoriteId: String = "",
    @SerializedName("tags")
    var tags: List<Any> = listOf()
)