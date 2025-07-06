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


import cc.sovellus.vrcaa.api.vrchat.http.models.World
import com.google.gson.annotations.SerializedName

data class FriendLocation(
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("location")
    val location: String,
    @SerializedName("travelingToLocation")
    val travelingToLocation: String,
    @SerializedName("user")
    val user: PartialFriend,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("world")
    val world: World?,
    @SerializedName("worldId")
    val worldId: String,
    @SerializedName("platform")
    var platform: String = ""
)