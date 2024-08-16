package cc.sovellus.vrcaa.api.search

import com.google.gson.annotations.SerializedName

data class SearchAvatar(
    @SerializedName("authorId")
    val authorId: String,
    @SerializedName("authorName")
    val authorName: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("imageUrl")
    val imageUrl: Any?,
    @SerializedName("name")
    val name: String,
    @SerializedName("releaseStatus")
    val releaseStatus: String,
    @SerializedName("thumbnailImageUrl")
    val thumbnailImageUrl: String
)