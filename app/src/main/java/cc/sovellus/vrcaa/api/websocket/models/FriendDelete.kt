package cc.sovellus.vrcaa.api.websocket.models


import com.google.gson.annotations.SerializedName

data class FriendDelete(
    @SerializedName("userId")
    val userId: String
)