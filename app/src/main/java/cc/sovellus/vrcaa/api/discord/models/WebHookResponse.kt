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

package cc.sovellus.vrcaa.api.discord.models


import com.google.gson.annotations.SerializedName

data class WebHookResponse(
    @SerializedName("attachments")
    val attachments: List<Any>,
    @SerializedName("author")
    val author: Author,
    @SerializedName("channel_id")
    val channelId: String,
    @SerializedName("components")
    val components: List<Any>,
    @SerializedName("content")
    val content: String,
    @SerializedName("edited_timestamp")
    val editedTimestamp: Any?,
    @SerializedName("embeds")
    val embeds: List<Embed>,
    @SerializedName("flags")
    val flags: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("mention_everyone")
    val mentionEveryone: Boolean,
    @SerializedName("mention_roles")
    val mentionRoles: List<Any>,
    @SerializedName("mentions")
    val mentions: List<Any>,
    @SerializedName("pinned")
    val pinned: Boolean,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("tts")
    val tts: Boolean,
    @SerializedName("type")
    val type: Int,
    @SerializedName("webhook_id")
    val webhookId: String
) {
    data class Author(
        @SerializedName("avatar")
        val avatar: Any?,
        @SerializedName("bot")
        val bot: Boolean,
        @SerializedName("clan")
        val clan: Any?,
        @SerializedName("discriminator")
        val discriminator: String,
        @SerializedName("flags")
        val flags: Int,
        @SerializedName("global_name")
        val globalName: Any?,
        @SerializedName("id")
        val id: String,
        @SerializedName("public_flags")
        val publicFlags: Int,
        @SerializedName("username")
        val username: String
    )

    data class Embed(
        @SerializedName("image")
        val image: Image,
        @SerializedName("type")
        val type: String
    ) {
        data class Image(
            @SerializedName("height")
            val height: Int,
            @SerializedName("proxy_url")
            val proxyUrl: String,
            @SerializedName("url")
            val url: String,
            @SerializedName("width")
            val width: Int
        )
    }
}