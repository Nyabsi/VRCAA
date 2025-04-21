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

data class Thread(
    @SerializedName("applied_tags")
    val appliedTags: List<String>?,
    @SerializedName("flags")
    val flags: Int,
    @SerializedName("guild_id")
    val guildId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("last_message_id")
    val lastMessageId: String,
    @SerializedName("last_pin_timestamp")
    val lastPinTimestamp: String?,
    @SerializedName("member")
    val member: Member,
    @SerializedName("member_count")
    val memberCount: Int,
    @SerializedName("member_ids_preview")
    val memberIdsPreview: List<String>,
    @SerializedName("message_count")
    val messageCount: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("owner_id")
    val ownerId: String,
    @SerializedName("parent_id")
    val parentId: String,
    @SerializedName("rate_limit_per_user")
    val rateLimitPerUser: Int,
    @SerializedName("thread_metadata")
    val threadMetadata: ThreadMetadata,
    @SerializedName("total_message_sent")
    val totalMessageSent: Int,
    @SerializedName("type")
    val type: Int
) {
    data class Member(
        @SerializedName("flags")
        val flags: Int,
        @SerializedName("join_timestamp")
        val joinTimestamp: String,
        @SerializedName("mute_config")
        val muteConfig: Any?,
        @SerializedName("muted")
        val muted: Boolean
    )

    data class ThreadMetadata(
        @SerializedName("archive_timestamp")
        val archiveTimestamp: String,
        @SerializedName("archived")
        val archived: Boolean,
        @SerializedName("auto_archive_duration")
        val autoArchiveDuration: Int,
        @SerializedName("create_timestamp")
        val createTimestamp: String,
        @SerializedName("invitable")
        val invitable: Boolean?,
        @SerializedName("locked")
        val locked: Boolean
    )
}