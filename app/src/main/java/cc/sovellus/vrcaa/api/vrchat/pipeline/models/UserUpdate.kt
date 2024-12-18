package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import cc.sovellus.vrcaa.api.vrchat.http.models.User
import com.google.gson.annotations.SerializedName

data class UserUpdate(
    @SerializedName("user")
    var user: User,
    @SerializedName("userId")
    var userId: String = ""
)