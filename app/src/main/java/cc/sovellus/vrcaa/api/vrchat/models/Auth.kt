package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class Auth(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("token")
    val token: String
)