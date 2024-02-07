package cc.sovellus.vrcaa.api.models.pipeline


import cc.sovellus.vrcaa.api.models.LimitedUser
import com.google.gson.annotations.SerializedName

data class FriendActive(
    @SerializedName("user")
    val user: LimitedUser,
    @SerializedName("userId")
    val userId: String
)