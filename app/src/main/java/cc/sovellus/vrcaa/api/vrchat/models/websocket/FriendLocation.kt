package cc.sovellus.vrcaa.api.vrchat.models.websocket


import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.World
import com.google.gson.annotations.SerializedName

data class FriendLocation(
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("location")
    val location: String?,
    @SerializedName("travelingToLocation")
    val travelingToLocation: String?,
    @SerializedName("user")
    val user: Friend,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("world")
    val world: World?,
    @SerializedName("worldId")
    val worldId: String
)