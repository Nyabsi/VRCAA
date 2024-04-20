package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Sticker(
    @SerializedName("asset")
    val asset: String?,
    @SerializedName("available")
    val available: Boolean,
    @SerializedName("description")
    val description: String?,
    @SerializedName("format_type")
    val formatType: Int,
    @SerializedName("guild_id")
    val guildId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("type")
    val type: Int
)