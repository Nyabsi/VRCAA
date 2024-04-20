package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Guild(
    @SerializedName("activity_instances")
    val activityInstances: ActivityInstances?,
    @SerializedName("application_command_counts")
    val applicationCommandCounts: ApplicationCommandCounts?,
    @SerializedName("channels")
    val channels: List<Channel>,
    @SerializedName("data_mode")
    val dataMode: String,
    @SerializedName("emojis")
    val emojis: List<Emoji>,
    @SerializedName("guild_scheduled_events")
    val guildScheduledEvents: List<GuildScheduledEvent>,
    @SerializedName("id")
    val id: String,
    @SerializedName("joined_at")
    val joinedAt: String,
    @SerializedName("large")
    val large: Boolean,
    @SerializedName("lazy")
    val lazy: Boolean,
    @SerializedName("member_count")
    val memberCount: Int,
    @SerializedName("premium_subscription_count")
    val premiumSubscriptionCount: Int,
    @SerializedName("properties")
    val properties: Properties,
    @SerializedName("roles")
    val roles: List<Role>,
    @SerializedName("stage_instances")
    val stageInstances: List<Any>,
    @SerializedName("stickers")
    val stickers: List<Sticker>,
    @SerializedName("threads")
    val threads: List<Thread>,
    @SerializedName("version")
    val version: Long
) {
    class ActivityInstances

    class ApplicationCommandCounts

    data class Channel(
        @SerializedName("available_tags")
        val availableTags: List<AvailableTag>?,
        @SerializedName("bitrate")
        val bitrate: Int?,
        @SerializedName("default_auto_archive_duration")
        val defaultAutoArchiveDuration: Int?,
        @SerializedName("default_forum_layout")
        val defaultForumLayout: Int?,
        @SerializedName("default_reaction_emoji")
        val defaultReactionEmoji: DefaultReactionEmoji?,
        @SerializedName("default_sort_order")
        val defaultSortOrder: Int?,
        @SerializedName("default_thread_rate_limit_per_user")
        val defaultThreadRateLimitPerUser: Int?,
        @SerializedName("flags")
        val flags: Int,
        @SerializedName("icon_emoji")
        val iconEmoji: IconEmoji?,
        @SerializedName("id")
        val id: String,
        @SerializedName("last_message_id")
        val lastMessageId: String?,
        @SerializedName("last_pin_timestamp")
        val lastPinTimestamp: String?,
        @SerializedName("name")
        val name: String,
        @SerializedName("nsfw")
        val nsfw: Boolean?,
        @SerializedName("parent_id")
        val parentId: String?,
        @SerializedName("permission_overwrites")
        val permissionOverwrites: List<PermissionOverwrite>,
        @SerializedName("position")
        val position: Int,
        @SerializedName("rate_limit_per_user")
        val rateLimitPerUser: Int?,
        @SerializedName("rtc_region")
        val rtcRegion: String?,
        @SerializedName("status")
        val status: Any?,
        @SerializedName("template")
        val template: String?,
        @SerializedName("theme_color")
        val themeColor: Int?,
        @SerializedName("topic")
        val topic: String?,
        @SerializedName("type")
        val type: Int,
        @SerializedName("user_limit")
        val userLimit: Int?,
        @SerializedName("video_quality_mode")
        val videoQualityMode: Int?,
        @SerializedName("voice_background_display")
        val voiceBackgroundDisplay: Any?
    ) {
        data class AvailableTag(
            @SerializedName("emoji_id")
            val emojiId: String?,
            @SerializedName("emoji_name")
            val emojiName: String?,
            @SerializedName("id")
            val id: String,
            @SerializedName("moderated")
            val moderated: Boolean,
            @SerializedName("name")
            val name: String
        )

        data class DefaultReactionEmoji(
            @SerializedName("emoji_id")
            val emojiId: String?,
            @SerializedName("emoji_name")
            val emojiName: String?
        )

        data class IconEmoji(
            @SerializedName("id")
            val id: String?,
            @SerializedName("name")
            val name: String
        )

        data class PermissionOverwrite(
            @SerializedName("allow")
            val allow: String,
            @SerializedName("deny")
            val deny: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("type")
            val type: Int
        )
    }
}