package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class UserGuildSettings(
    @SerializedName("entries")
    val entries: List<Entry>,
    @SerializedName("partial")
    val partial: Boolean,
    @SerializedName("version")
    val version: Int
) {
    data class Entry(
        @SerializedName("channel_overrides")
        val channelOverrides: List<ChannelOverride>,
        @SerializedName("flags")
        val flags: Int,
        @SerializedName("guild_id")
        val guildId: String?,
        @SerializedName("hide_muted_channels")
        val hideMutedChannels: Boolean,
        @SerializedName("message_notifications")
        val messageNotifications: Int,
        @SerializedName("mobile_push")
        val mobilePush: Boolean,
        @SerializedName("mute_config")
        val muteConfig: Any?,
        @SerializedName("mute_scheduled_events")
        val muteScheduledEvents: Boolean,
        @SerializedName("muted")
        val muted: Boolean,
        @SerializedName("notify_highlights")
        val notifyHighlights: Int,
        @SerializedName("suppress_everyone")
        val suppressEveryone: Boolean,
        @SerializedName("suppress_roles")
        val suppressRoles: Boolean,
        @SerializedName("version")
        val version: Int
    ) {
        data class ChannelOverride(
            @SerializedName("channel_id")
            val channelId: String,
            @SerializedName("collapsed")
            val collapsed: Boolean,
            @SerializedName("flags")
            val flags: Int?,
            @SerializedName("message_notifications")
            val messageNotifications: Int,
            @SerializedName("mute_config")
            val muteConfig: MuteConfig?,
            @SerializedName("muted")
            val muted: Boolean
        ) {
            data class MuteConfig(
                @SerializedName("end_time")
                val endTime: String,
                @SerializedName("selected_time_window")
                val selectedTimeWindow: Int
            )
        }
    }
}