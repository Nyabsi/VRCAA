package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("assetUrl")
    var assetUrl: String = "",
    @SerializedName("authorId")
    var authorId: String = "",
    @SerializedName("authorName")
    var authorName: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("featured")
    var featured: Boolean = false,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("pendingUpload")
    var pendingUpload: Boolean = false,
    @SerializedName("releaseStatus")
    var releaseStatus: String = "",
    @SerializedName("styles")
    var styles: Styles = Styles(),
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("thumbnailImageUrl")
    var thumbnailImageUrl: String = "",
    @SerializedName("unityPackageUrl")
    var unityPackageUrl: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("version")
    var version: Long = 0
)