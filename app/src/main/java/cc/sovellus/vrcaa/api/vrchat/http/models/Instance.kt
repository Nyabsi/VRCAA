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

data class Instance(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("clientNumber")
    val clientNumber: String,
    @SerializedName("closedAt")
    val closedAt: Any?,
    @SerializedName("displayName")
    val displayName: Any?,
    @SerializedName("full")
    val full: Boolean,
    @SerializedName("gameServerVersion")
    val gameServerVersion: Int,
    @SerializedName("hardClose")
    val hardClose: Any?,
    @SerializedName("hasCapacityForYou")
    val hasCapacityForYou: Boolean,
    @SerializedName("hidden")
    val hidden: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("instanceId")
    val instanceId: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("n_users")
    val nUsers: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("permanent")
    val permanent: Boolean,
    @SerializedName("photonRegion")
    val photonRegion: String,
    @SerializedName("platforms")
    val platforms: Platforms,
    @SerializedName("queueEnabled")
    val queueEnabled: Boolean,
    @SerializedName("recommendedCapacity")
    val recommendedCapacity: Int,
    @SerializedName("region")
    val region: String,
    @SerializedName("secureName")
    val secureName: String,
    @SerializedName("shortName")
    val shortName: Any?,
    @SerializedName("strict")
    val strict: Boolean,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("type")
    val type: String,
    @SerializedName("userCount")
    val userCount: Int,
    @SerializedName("world")
    val world: World,
    @SerializedName("worldId")
    val worldId: String
) {
    data class Platforms(
        @SerializedName("android")
        val android: Int,
        @SerializedName("standalonewindows")
        val standalonewindows: Int
    )
}