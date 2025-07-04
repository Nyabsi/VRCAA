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

data class User(
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("avatar_decoration_data")
    val avatarDecorationData: AvatarDecorationData?,
    @SerializedName("bot")
    val bot: Boolean?,
    @SerializedName("clan")
    val clan: Any?,
    @SerializedName("discriminator")
    val discriminator: String,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("global_name")
    val globalName: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("public_flags")
    val publicFlags: Int,
    @SerializedName("username")
    val username: String
) {
    data class AvatarDecorationData(
        @SerializedName("asset")
        val asset: String,
        @SerializedName("sku_id")
        val skuId: String
    )
}