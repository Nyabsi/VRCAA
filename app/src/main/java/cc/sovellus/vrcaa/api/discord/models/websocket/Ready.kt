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

data class Ready(
    @SerializedName("analytics_token")
    val analyticsToken: String,
    @SerializedName("api_code_version")
    val apiCodeVersion: Int,
    @SerializedName("auth")
    val auth: Auth,
    @SerializedName("auth_session_id_hash")
    val authSessionIdHash: String,
    @SerializedName("connected_accounts")
    val connectedAccounts: List<Any>,
    @SerializedName("consents")
    val consents: Consents,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("experiments")
    val experiments: List<List<Long>>,
    @SerializedName("friend_suggestion_count")
    val friendSuggestionCount: Int,
    @SerializedName("geo_ordered_rtc_regions")
    val geoOrderedRtcRegions: List<String>,
    @SerializedName("guild_experiments")
    val guildExperiments: List<List<Any>>,
    @SerializedName("guild_join_requests")
    val guildJoinRequests: List<GuildJoinRequest>,
    @SerializedName("guilds")
    val guilds: List<Guild>,
    @SerializedName("merged_members")
    val mergedMembers: List<List<MergedMember>>,
    @SerializedName("notification_settings")
    val notificationSettings: NotificationSettings,
    @SerializedName("private_channels")
    val privateChannels: List<PrivateChannel>,
    @SerializedName("read_state")
    val readState: ReadState,
    @SerializedName("relationships")
    val relationships: List<Relationship>,
    @SerializedName("resume_gateway_url")
    val resumeGatewayUrl: String,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("session_type")
    val sessionType: String,
    @SerializedName("sessions")
    val sessions: List<Session>,
    @SerializedName("_trace")
    val trace: List<String>,
    @SerializedName("tutorial")
    val tutorial: Any?,
    @SerializedName("user")
    val user: CurrentUser,
    @SerializedName("user_guild_settings")
    val userGuildSettings: UserGuildSettings,
    @SerializedName("user_settings_proto")
    val userSettingsProto: String,
    @SerializedName("users")
    val users: List<User>,
    @SerializedName("v")
    val v: Int
)