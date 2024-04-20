package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class GuildScheduledEvent(
    @SerializedName("auto_start")
    val autoStart: Boolean,
    @SerializedName("channel_id")
    val channelId: String?,
    @SerializedName("creator_id")
    val creatorId: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("entity_id")
    val entityId: String?,
    @SerializedName("entity_metadata")
    val entityMetadata: EntityMetadata,
    @SerializedName("entity_type")
    val entityType: Int,
    @SerializedName("guild_id")
    val guildId: String,
    @SerializedName("guild_scheduled_event_exceptions")
    val guildScheduledEventExceptions: List<GuildScheduledEventException>,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("privacy_level")
    val privacyLevel: Int,
    @SerializedName("recurrence_rule")
    val recurrenceRule: RecurrenceRule?,
    @SerializedName("scheduled_end_time")
    val scheduledEndTime: String?,
    @SerializedName("scheduled_start_time")
    val scheduledStartTime: String,
    @SerializedName("sku_ids")
    val skuIds: List<Any>,
    @SerializedName("status")
    val status: Int
) {
    data class EntityMetadata(
        @SerializedName("location")
        val location: String?,
        @SerializedName("speaker_ids")
        val speakerIds: List<Any>?
    )

    data class GuildScheduledEventException(
        @SerializedName("event_exception_id")
        val eventExceptionId: String,
        @SerializedName("event_id")
        val eventId: String,
        @SerializedName("guild_id")
        val guildId: String,
        @SerializedName("is_canceled")
        val isCanceled: Boolean,
        @SerializedName("scheduled_end_time")
        val scheduledEndTime: String,
        @SerializedName("scheduled_start_time")
        val scheduledStartTime: Any?
    )

    data class RecurrenceRule(
        @SerializedName("by_month")
        val byMonth: Any?,
        @SerializedName("by_month_day")
        val byMonthDay: Any?,
        @SerializedName("by_n_weekday")
        val byNWeekday: List<ByNWeekday>?,
        @SerializedName("by_weekday")
        val byWeekday: List<Int>?,
        @SerializedName("by_year_day")
        val byYearDay: Any?,
        @SerializedName("count")
        val count: Any?,
        @SerializedName("end")
        val end: Any?,
        @SerializedName("frequency")
        val frequency: Int,
        @SerializedName("interval")
        val interval: Int,
        @SerializedName("start")
        val start: String
    ) {
        data class ByNWeekday(
            @SerializedName("day")
            val day: Int,
            @SerializedName("n")
            val n: Int
        )
    }
}