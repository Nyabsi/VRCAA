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

data class LimitedUser(
    @SerializedName("ageVerificationStatus")
    var ageVerificationStatus: String = "",
    @SerializedName("ageVerified")
    var ageVerified: Boolean = false,
    @SerializedName("allowAvatarCopying")
    var allowAvatarCopying: Boolean = false,
    @SerializedName("badges")
    var badges: List<Badge> = listOf(),
    @SerializedName("bio")
    var bio: String = "",
    @SerializedName("bioLinks")
    var bioLinks: List<String> = listOf(),
    @SerializedName("currentAvatarImageUrl")
    var currentAvatarImageUrl: String = "",
    @SerializedName("currentAvatarTags")
    var currentAvatarTags: List<Any> = listOf(),
    @SerializedName("currentAvatarThumbnailImageUrl")
    var currentAvatarThumbnailImageUrl: String = "",
    @SerializedName("date_joined")
    var dateJoined: String = "",
    @SerializedName("developerType")
    var developerType: String = "",
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("friendKey")
    var friendKey: String = "",
    @SerializedName("friendRequestStatus")
    var friendRequestStatus: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("instanceId")
    var instanceId: String = "",
    @SerializedName("isFriend")
    var isFriend: Boolean = false,
    @SerializedName("last_activity")
    var lastActivity: String = "",
    @SerializedName("last_login")
    var lastLogin: String = "",
    @SerializedName("last_mobile")
    var lastMobile: Any = Any(),
    @SerializedName("last_platform")
    var lastPlatform: String = "",
    @SerializedName("location")
    var location: String = "",
    @SerializedName("note")
    var note: String = "",
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("profilePicOverride")
    var profilePicOverride: String = "",
    @SerializedName("profilePicOverrideThumbnail")
    var profilePicOverrideThumbnail: String = "",
    @SerializedName("pronouns")
    var pronouns: String = "",
    @SerializedName("state")
    var state: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("statusDescription")
    var statusDescription: String = "",
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("travelingToInstance")
    var travelingToInstance: String = "",
    @SerializedName("travelingToLocation")
    var travelingToLocation: String = "",
    @SerializedName("travelingToWorld")
    var travelingToWorld: String = "",
    @SerializedName("userIcon")
    var userIcon: String = "",
    @SerializedName("worldId")
    var worldId: String = ""
)