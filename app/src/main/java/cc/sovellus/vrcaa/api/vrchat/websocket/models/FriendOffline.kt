package cc.sovellus.vrcaa.api.vrchat.websocket.models


import com.google.gson.annotations.SerializedName

data class FriendOffline(
    @SerializedName("userId")
    val userId: String
)