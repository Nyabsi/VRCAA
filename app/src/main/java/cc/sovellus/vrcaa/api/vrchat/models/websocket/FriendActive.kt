package cc.sovellus.vrcaa.api.vrchat.models.websocket


import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import com.google.gson.annotations.SerializedName

data class FriendActive(
    @SerializedName("user")
    val user: LimitedUser,
    @SerializedName("userId")
    val userId: String
)