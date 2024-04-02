package cc.sovellus.vrcaa.api.vrchat.websocket.models


import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import com.google.gson.annotations.SerializedName

data class FriendOnline(
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("location")
    val location: String,
    @SerializedName("travelingToLocation")
    val travelingToLocation: String,
    @SerializedName("user")
    val user: LimitedUser,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("worldId")
    val worldId: String
)