package cc.sovellus.vrcaa.api.search.avtrdb.models


import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("author")
    var author: Author = Author(),
    @SerializedName("compatibility")
    var compatibility: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("image_url")
    var imageUrl: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("thumbnail_image_url")
    var thumbnailImageUrl: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("vrc_id")
    var vrcId: String = ""
)