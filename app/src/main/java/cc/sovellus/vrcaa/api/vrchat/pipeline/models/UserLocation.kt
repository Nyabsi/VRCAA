package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import com.google.gson.annotations.SerializedName

data class UserLocation(
    @SerializedName("userId")
    val userId: Boolean,
    @SerializedName("user")
    val user: User,
    @SerializedName("location")
    val location: String,
    @SerializedName("instance")
    val instance: String
)