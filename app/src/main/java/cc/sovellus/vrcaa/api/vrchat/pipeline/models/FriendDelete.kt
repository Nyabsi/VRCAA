package cc.sovellus.vrcaa.api.vrchat.models.websocket


import com.google.gson.annotations.SerializedName

data class FriendDelete(
    @SerializedName("userId")
    val userId: String
)