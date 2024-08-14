package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class FavoriteAdd(
    @SerializedName("favoriteId")
    var favoriteId: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("type")
    var type: String = ""
)