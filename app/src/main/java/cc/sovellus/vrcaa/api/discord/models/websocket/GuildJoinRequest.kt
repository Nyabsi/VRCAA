package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class GuildJoinRequest(
    @SerializedName("application_status")
    val applicationStatus: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("guild_id")
    val guildId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("interview_channel_id")
    val interviewChannelId: Any?,
    @SerializedName("join_request_id")
    val joinRequestId: String,
    @SerializedName("last_seen")
    val lastSeen: Any?,
    @SerializedName("rejection_reason")
    val rejectionReason: Any?,
    @SerializedName("user_id")
    val userId: String
)