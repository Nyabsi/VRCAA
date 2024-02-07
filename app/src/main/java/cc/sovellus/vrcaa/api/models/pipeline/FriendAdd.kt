package cc.sovellus.vrcaa.api.models.pipeline


import cc.sovellus.vrcaa.api.models.LimitedUser
import com.google.gson.annotations.SerializedName

data class FriendAdd(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("user")
    val user: LimitedUser
)