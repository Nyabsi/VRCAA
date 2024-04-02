package cc.sovellus.vrcaa.api.vrchat.websocket.models


import com.google.gson.annotations.SerializedName

data class FriendDelete(
    @SerializedName("userId")
    val userId: String
)