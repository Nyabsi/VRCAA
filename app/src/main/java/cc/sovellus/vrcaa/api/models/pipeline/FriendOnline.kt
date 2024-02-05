package cc.sovellus.vrcaa.api.models.pipeline


import com.google.gson.annotations.SerializedName

data class FriendOnline(
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("location")
    val location: String,
    @SerializedName("travelingToLocation")
    val travelingToLocation: String,
    @SerializedName("user")
    val user: UserBase,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("worldId")
    val worldId: String
)