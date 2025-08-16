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
    var attachments: List<Any> = listOf(),
    @SerializedName("author")
    var author: Author = Author(),
    @SerializedName("channel_id")
    var channelId: String = "",
    @SerializedName("components")
    var components: List<Any> = listOf(),
    @SerializedName("content")
    var content: String = "",
    @SerializedName("edited_timestamp")
    var editedTimestamp: Any = Any(),
    @SerializedName("embeds")
    var embeds: List<Embed> = listOf(),
    @SerializedName("flags")
    var flags: Int = 0,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("mention_everyone")
    var mentionEveryone: Boolean = false,
    @SerializedName("mention_roles")
    var mentionRoles: List<Any> = listOf(),
    @SerializedName("mentions")
    var mentions: List<Any> = listOf(),
    @SerializedName("pinned")
    var pinned: Boolean = false,
    @SerializedName("timestamp")
    var timestamp: String = "",
    @SerializedName("tts")
    var tts: Boolean = false,
    @SerializedName("type")
    var type: Int = 0,
    @SerializedName("webhook_id")
    var webhookId: String = ""
) {
    data class Author(
        @SerializedName("avatar")
        var avatar: Any = Any(),
        @SerializedName("bot")
        var bot: Boolean = false,
        @SerializedName("clan")
        var clan: Any = Any(),
        @SerializedName("discriminator")
        var discriminator: String = "",
        @SerializedName("flags")
        var flags: Int = 0,
        @SerializedName("global_name")
        var globalName: Any = Any(),
        @SerializedName("id")
        var id: String = "",
        @SerializedName("primary_guild")
        var primaryGuild: Any = Any(),
        @SerializedName("public_flags")
        var publicFlags: Int = 0,
        @SerializedName("username")
        var username: String = ""
    )

    data class Embed(
        @SerializedName("content_scan_version")
        var contentScanVersion: Int = 0,
        @SerializedName("image")
        var image: Image = Image(),
        @SerializedName("type")
        var type: String = ""
    ) {
        data class Image(
            @SerializedName("content_type")
            var contentType: String = "",
            @SerializedName("flags")
            var flags: Int = 0,
            @SerializedName("height")
            var height: Int = 0,
            @SerializedName("placeholder")
            var placeholder: String = "",
            @SerializedName("placeholder_version")
            var placeholderVersion: Int = 0,
            @SerializedName("proxy_url")
            var proxyUrl: String = "",
            @SerializedName("url")
            var url: String = "",
            @SerializedName("width")
            var width: Int = 0
        )
    }
}