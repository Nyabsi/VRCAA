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

package cc.sovellus.vrcaa.api.vrchat.http.models

import com.google.gson.annotations.SerializedName

data class UserGroup(
    @SerializedName("bannerId")
    val bannerId: String,
    @SerializedName("bannerUrl")
    val bannerUrl: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discriminator")
    val discriminator: String,
    @SerializedName("groupId")
    val groupId: String,
    @SerializedName("iconId")
    val iconId: String?,
    @SerializedName("iconUrl")
    val iconUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isRepresenting")
    val isRepresenting: Boolean?,
    @SerializedName("memberCount")
    val memberCount: Int,
    @SerializedName("memberVisibility")
    val memberVisibility: String,
    @SerializedName("mutualGroup")
    val mutualGroup: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("privacy")
    val privacy: String,
    @SerializedName("shortCode")
    val shortCode: String
)