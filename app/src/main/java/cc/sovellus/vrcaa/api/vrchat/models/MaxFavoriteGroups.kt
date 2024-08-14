package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class MaxFavoriteGroups(
    @SerializedName("avatar")
    var avatar: Int = 0,
    @SerializedName("friend")
    var friend: Int = 0,
    @SerializedName("world")
    var world: Int = 0
)