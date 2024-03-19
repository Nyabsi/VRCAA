package cc.sovellus.vrcaa.api.websocket.models


import cc.sovellus.vrcaa.api.http.models.LimitedUser
import com.google.gson.annotations.SerializedName

data class FriendAdd(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("user")
    val user: LimitedUser
)