package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import com.google.gson.annotations.SerializedName

data class FriendAdd(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("user")
    val user: Friend
)