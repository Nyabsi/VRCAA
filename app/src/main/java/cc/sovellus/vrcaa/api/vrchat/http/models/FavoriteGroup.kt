package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class FavoriteGroup(
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("ownerDisplayName")
    var ownerDisplayName: String = "",
    @SerializedName("ownerId")
    var ownerId: String = "",
    @SerializedName("tags")
    var tags: List<Any> = listOf(),
    @SerializedName("type")
    var type: String = "",
    @SerializedName("visibility")
    var visibility: String = ""
)