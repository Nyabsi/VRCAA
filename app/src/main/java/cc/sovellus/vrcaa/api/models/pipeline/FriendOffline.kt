package cc.sovellus.vrcaa.api.models.pipeline


import com.google.gson.annotations.SerializedName

data class FriendOffline(
    @SerializedName("userId")
    val userId: String
)