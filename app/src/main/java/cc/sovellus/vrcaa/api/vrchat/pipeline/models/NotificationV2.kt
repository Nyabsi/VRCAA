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

package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import com.google.gson.annotations.SerializedName

data class NotificationV2(
    @SerializedName("canDelete")
    var canDelete: Boolean = false,
    @SerializedName("category")
    var category: String = "",
    @SerializedName("createdAt")
    var createdAt: String = "",
    @SerializedName("data")
    var `data`: Any = Any(),
    @SerializedName("expiresAt")
    var expiresAt: String = "",
    @SerializedName("expiryAfterSeen")
    var expiryAfterSeen: Int = 0,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("ignoreDND")
    var ignoreDND: Boolean = false,
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("isSystem")
    var isSystem: Boolean = false,
    @SerializedName("link")
    var link: String = "",
    @SerializedName("linkText")
    var linkText: String = "",
    @SerializedName("linkTextKey")
    var linkTextKey: String = "",
    @SerializedName("message")
    var message: String = "",
    @SerializedName("messageKey")
    var messageKey: String?,
    @SerializedName("receiverUserId")
    var receiverUserId: String = "",
    @SerializedName("relatedNotificationsId")
    var relatedNotificationsId: String = "",
    @SerializedName("requireSeen")
    var requireSeen: Boolean = false,
    @SerializedName("responses")
    var responses: List<Response> = listOf(),
    @SerializedName("seen")
    var seen: Boolean = false,
    @SerializedName("senderUserId")
    var senderUserId: String?,
    @SerializedName("senderUsername")
    var senderUsername: String?,
    @SerializedName("title")
    var title: String = "",
    @SerializedName("titleKey")
    var titleKey: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("updatedAt")
    var updatedAt: String = "",
    @SerializedName("version")
    var version: Int = 0
) {
    data class Response(
        @SerializedName("data")
        var `data`: String = "",
        @SerializedName("icon")
        var icon: String = "",
        @SerializedName("text")
        var text: String = "",
        @SerializedName("textKey")
        var textKey: String = "",
        @SerializedName("type")
        var type: String = ""
    )
}