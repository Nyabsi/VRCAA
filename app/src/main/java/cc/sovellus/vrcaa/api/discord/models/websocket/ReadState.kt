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

data class ReadState(
    @SerializedName("entries")
    val entries: List<Entry>,
    @SerializedName("partial")
    val partial: Boolean,
    @SerializedName("version")
    val version: Int
) {
    data class Entry(
        @SerializedName("badge_count")
        val badgeCount: Int?,
        @SerializedName("flags")
        val flags: Int?,
        @SerializedName("id")
        val id: String,
        @SerializedName("last_acked_id")
        val lastAckedId: String?,
        @SerializedName("last_message_id")
        val lastMessageId: String?,
        @SerializedName("last_pin_timestamp")
        val lastPinTimestamp: String?,
        @SerializedName("last_viewed")
        val lastViewed: Int?,
        @SerializedName("mention_count")
        val mentionCount: Int?,
        @SerializedName("read_state_type")
        val readStateType: Int?
    )
}