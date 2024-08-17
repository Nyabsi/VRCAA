package cc.sovellus.vrcaa.api.vrchat.models.websocket


import cc.sovellus.vrcaa.api.vrchat.models.User
import com.google.gson.annotations.SerializedName

data class UserUpdate(
    @SerializedName("user")
    var user: User,
    @SerializedName("userId")
    var userId: String = ""
)