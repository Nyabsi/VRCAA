/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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