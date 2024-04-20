package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Emoji(
    @SerializedName("animated")
    val animated: Boolean,
    @SerializedName("available")
    val available: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("managed")
    val managed: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("require_colons")
    val requireColons: Boolean,
    @SerializedName("roles")
    val roles: List<String>
)