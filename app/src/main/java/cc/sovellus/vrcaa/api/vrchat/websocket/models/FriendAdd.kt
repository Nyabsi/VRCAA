package cc.sovellus.vrcaa.api.vrchat.websocket.models


import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import com.google.gson.annotations.SerializedName

data class FriendAdd(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("user")
    val user: LimitedUser
)