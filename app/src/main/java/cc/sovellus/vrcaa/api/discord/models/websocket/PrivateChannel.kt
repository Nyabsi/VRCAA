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

data class PrivateChannel(
    @SerializedName("flags")
    val flags: Int,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("is_message_request")
    val isMessageRequest: Boolean?,
    @SerializedName("is_message_request_timestamp")
    val isMessageRequestTimestamp: String?,
    @SerializedName("is_spam")
    val isSpam: Boolean?,
    @SerializedName("last_message_id")
    val lastMessageId: String?,
    @SerializedName("last_pin_timestamp")
    val lastPinTimestamp: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("owner_id")
    val ownerId: String?,
    @SerializedName("recipient_ids")
    val recipientIds: List<String>,
    @SerializedName("safety_warnings")
    val safetyWarnings: List<Any>?,
    @SerializedName("type")
    val type: Int
)