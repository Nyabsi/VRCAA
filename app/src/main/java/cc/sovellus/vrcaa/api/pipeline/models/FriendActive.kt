package cc.sovellus.vrcaa.api.pipeline.models


import cc.sovellus.vrcaa.api.http.models.LimitedUser
import com.google.gson.annotations.SerializedName

data class FriendActive(
    @SerializedName("user")
    val user: LimitedUser,
    @SerializedName("userId")
    val userId: String
)