package cc.sovellus.vrcaa.api.vrchat.models.websocket

import com.google.gson.annotations.SerializedName

data class FriendUpdate(
    @SerializedName("user")
    val user: UpdateUser,
    @SerializedName("userId")
    val userId: String
)