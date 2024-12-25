package cc.sovellus.vrcaa.api.vrchat.pipeline.models

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import com.google.gson.annotations.SerializedName

data class FriendUpdate(
    @SerializedName("user")
    val user: Friend,
    @SerializedName("userId")
    val userId: String
)