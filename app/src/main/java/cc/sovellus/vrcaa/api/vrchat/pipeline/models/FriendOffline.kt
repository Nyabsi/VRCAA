package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import com.google.gson.annotations.SerializedName

data class FriendOffline(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("platform")
    val platform: String
)