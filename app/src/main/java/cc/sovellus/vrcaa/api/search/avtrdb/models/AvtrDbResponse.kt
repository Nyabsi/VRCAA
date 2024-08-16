package cc.sovellus.vrcaa.api.search.avtrdb.models


import com.google.gson.annotations.SerializedName

data class AvtrDbResponse(
    @SerializedName("avatars")
    var avatars: List<Avatar> = listOf(),
    @SerializedName("has_more")
    var hasMore: Boolean = false
)