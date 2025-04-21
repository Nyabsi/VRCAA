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

data class Role(
    @SerializedName("color")
    val color: Int,
    @SerializedName("flags")
    val flags: Int,
    @SerializedName("hoist")
    val hoist: Boolean,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("managed")
    val managed: Boolean,
    @SerializedName("mentionable")
    val mentionable: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("permissions")
    val permissions: String,
    @SerializedName("position")
    val position: Int,
    @SerializedName("tags")
    val tags: Tags?,
    @SerializedName("unicode_emoji")
    val unicodeEmoji: String?
) {
    data class Tags(
        @SerializedName("bot_id")
        val botId: String?,
        @SerializedName("guild_connections")
        val guildConnections: Any?,
        @SerializedName("integration_id")
        val integrationId: String?,
        @SerializedName("premium_subscriber")
        val premiumSubscriber: Any?
    )
}