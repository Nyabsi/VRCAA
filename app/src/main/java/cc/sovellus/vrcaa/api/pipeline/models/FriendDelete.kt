package cc.sovellus.vrcaa.api.pipeline.models


import com.google.gson.annotations.SerializedName

data class FriendDelete(
    @SerializedName("userId")
    val userId: String
)