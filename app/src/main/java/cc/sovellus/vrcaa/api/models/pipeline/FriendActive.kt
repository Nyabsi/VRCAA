package cc.sovellus.vrcaa.api.models.pipeline


import com.google.gson.annotations.SerializedName

data class FriendActive(
    @SerializedName("user")
    val user: UserBase,
    @SerializedName("userId")
    val userId: String
)