package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class MergedMember(
    @SerializedName("avatar")
    val avatar: Any?,
    @SerializedName("communication_disabled_until")
    val communicationDisabledUntil: Any?,
    @SerializedName("deaf")
    val deaf: Boolean,
    @SerializedName("flags")
    val flags: Int,
    @SerializedName("joined_at")
    val joinedAt: String,
    @SerializedName("mute")
    val mute: Boolean,
    @SerializedName("nick")
    val nick: String?,
    @SerializedName("pending")
    val pending: Boolean,
    @SerializedName("premium_since")
    val premiumSince: Any?,
    @SerializedName("roles")
    val roles: List<String>,
    @SerializedName("user_id")
    val userId: String
)