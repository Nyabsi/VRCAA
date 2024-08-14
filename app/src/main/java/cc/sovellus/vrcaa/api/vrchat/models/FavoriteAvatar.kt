package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class FavoriteAvatar(
    @SerializedName("authorId")
    var authorId: String = "",
    @SerializedName("authorName")
    var authorName: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("favoriteGroup")
    var favoriteGroup: String = "",
    @SerializedName("favoriteId")
    var favoriteId: String = "",
    @SerializedName("featured")
    var featured: Boolean = false,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("releaseStatus")
    var releaseStatus: String = "",
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("thumbnailImageUrl")
    var thumbnailImageUrl: String = "",
    @SerializedName("unityPackageUrl")
    var unityPackageUrl: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("version")
    var version: Int = 0
)