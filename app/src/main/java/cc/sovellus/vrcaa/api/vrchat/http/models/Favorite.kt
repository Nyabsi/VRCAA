package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Favorite(
    @SerializedName("favoriteId")
    val favoriteId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("type")
    val type: String
)