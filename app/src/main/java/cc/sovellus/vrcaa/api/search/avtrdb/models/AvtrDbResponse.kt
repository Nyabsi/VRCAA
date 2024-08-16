package cc.sovellus.vrcaa.api.search.avtrdb.models


import cc.sovellus.vrcaa.api.search.SearchAvatar
import com.google.gson.annotations.SerializedName

data class AvtrDbResponse(
    @SerializedName("avatars")
    var avatars: List<SearchAvatar> = listOf(),
    @SerializedName("has_more")
    var hasMore: Boolean = false
)