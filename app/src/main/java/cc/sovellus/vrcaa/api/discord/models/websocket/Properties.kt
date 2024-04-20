package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("afk_channel_id")
    val afkChannelId: String?,
    @SerializedName("afk_timeout")
    val afkTimeout: Int,
    @SerializedName("application_id")
    val applicationId: Any?,
    @SerializedName("banner")
    val banner: String?,
    @SerializedName("clan")
    val clan: Any?,
    @SerializedName("default_message_notifications")
    val defaultMessageNotifications: Int,
    @SerializedName("description")
    val description: String?,
    @SerializedName("discovery_splash")
    val discoverySplash: String?,
    @SerializedName("explicit_content_filter")
    val explicitContentFilter: Int,
    @SerializedName("features")
    val features: List<String>,
    @SerializedName("home_header")
    val homeHeader: String?,
    @SerializedName("hub_type")
    val hubType: Any?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("incidents_data")
    val incidentsData: Any?,
    @SerializedName("latest_onboarding_question_id")
    val latestOnboardingQuestionId: String?,
    @SerializedName("max_members")
    val maxMembers: Int,
    @SerializedName("max_stage_video_channel_users")
    val maxStageVideoChannelUsers: Int,
    @SerializedName("max_video_channel_users")
    val maxVideoChannelUsers: Int,
    @SerializedName("mfa_level")
    val mfaLevel: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("nsfw")
    val nsfw: Boolean,
    @SerializedName("nsfw_level")
    val nsfwLevel: Int,
    @SerializedName("owner_id")
    val ownerId: String,
    @SerializedName("preferred_locale")
    val preferredLocale: String,
    @SerializedName("premium_progress_bar_enabled")
    val premiumProgressBarEnabled: Boolean,
    @SerializedName("premium_tier")
    val premiumTier: Int,
    @SerializedName("public_updates_channel_id")
    val publicUpdatesChannelId: String?,
    @SerializedName("rules_channel_id")
    val rulesChannelId: String?,
    @SerializedName("safety_alerts_channel_id")
    val safetyAlertsChannelId: String?,
    @SerializedName("splash")
    val splash: String?,
    @SerializedName("system_channel_flags")
    val systemChannelFlags: Int,
    @SerializedName("system_channel_id")
    val systemChannelId: String?,
    @SerializedName("vanity_url_code")
    val vanityUrlCode: String?,
    @SerializedName("verification_level")
    val verificationLevel: Int
)