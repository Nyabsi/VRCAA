package cc.sovellus.vrcaa.api.vrchat.models.websocket


import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import com.google.gson.annotations.SerializedName

data class UserLocation(
    @SerializedName("userId")
    val userId: Boolean,
    @SerializedName("user")
    val user: User,
    @SerializedName("location")
    val location: String,
    @SerializedName("instance")
    val instance: String,
    @SerializedName("worldId")
    val worldId: String,
    @SerializedName("world")
    val world: World?
)