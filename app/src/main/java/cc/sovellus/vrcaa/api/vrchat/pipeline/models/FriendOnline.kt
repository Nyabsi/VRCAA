package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import com.google.gson.annotations.SerializedName

data class FriendOnline(
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("location")
    val location: String,
    @SerializedName("travelingToLocation")
    val travelingToLocation: String,
    @SerializedName("user")
    val user: Friend,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("worldId")
    val worldId: String,
    @SerializedName("platform")
    var platform: String = ""
)