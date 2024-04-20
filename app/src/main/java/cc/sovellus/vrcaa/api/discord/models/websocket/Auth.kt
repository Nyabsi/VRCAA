package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Auth(
    @SerializedName("authenticator_types")
    val authenticatorTypes: List<Int>
)