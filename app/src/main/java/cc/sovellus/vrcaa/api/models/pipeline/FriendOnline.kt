package cc.sovellus.vrcaa.api.models.pipeline


import cc.sovellus.vrcaa.api.models.LimitedUser
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