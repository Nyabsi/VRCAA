package cc.sovellus.vrcaa.api.search

import com.google.gson.annotations.SerializedName

data class SearchAvatar(
    @SerializedName("id")
    val id: String,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("authorId")
    val authorId: String = "",
    @SerializedName("authorName")
    val authorName: String = "",
)