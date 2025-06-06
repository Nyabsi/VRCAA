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
    val canDelete: Boolean,
    @SerializedName("category")
    val category: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("data")
    val `data`: Any?,
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("expiryAfterSeen")
    val expiryAfterSeen: Any?,
    @SerializedName("id")
    val id: String,
    @SerializedName("ignoreDND")
    val ignoreDND: Boolean,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("isSystem")
    val isSystem: Boolean,
    @SerializedName("link")
    val link: String,
    @SerializedName("linkText")
    val linkText: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("messageKey")
    val messageKey: String,
    @SerializedName("receiverUserId")
    val receiverUserId: String,
    @SerializedName("relatedNotificationsId")
    val relatedNotificationsId: String,
    @SerializedName("requireSeen")
    val requireSeen: Boolean,
    @SerializedName("responses")
    val responses: List<Response>,
    @SerializedName("seen")
    val seen: Boolean,
    @SerializedName("senderUserId")
    val senderUserId: String,
    @SerializedName("senderUsername")
    val senderUsername: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("titleKey")
    val titleKey: Any?,
    @SerializedName("type")
    val type: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("version")
    val version: Int
) {
    data class Response(
        @SerializedName("data")
        val `data`: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("text")
        val text: String,
        @SerializedName("textKey")
        val textKey: String,
        @SerializedName("type")
        val type: String
    )
}