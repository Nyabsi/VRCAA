package cc.sovellus.vrcaa.api.websocket.models


import com.google.gson.annotations.SerializedName

data class FriendOffline(
    @SerializedName("userId")
    val userId: String
)