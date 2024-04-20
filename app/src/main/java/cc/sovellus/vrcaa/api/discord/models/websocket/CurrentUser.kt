package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class CurrentUser(
    @SerializedName("accent_color")
    val accentColor: Any?,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("avatar_decoration_data")
    val avatarDecorationData: Any?,
    @SerializedName("banner")
    val banner: String,
    @SerializedName("banner_color")
    val bannerColor: Any?,
    @SerializedName("bio")
    val bio: String,
    @SerializedName("clan")
    val clan: Any?,
    @SerializedName("desktop")
    val desktop: Boolean,
    @SerializedName("discriminator")
    val discriminator: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("flags")
    val flags: Int,
    @SerializedName("global_name")
    val globalName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("mfa_enabled")
    val mfaEnabled: Boolean,
    @SerializedName("mobile")
    val mobile: Boolean,
    @SerializedName("nsfw_allowed")
    val nsfwAllowed: Boolean,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("premium")
    val premium: Boolean,
    @SerializedName("premium_type")
    val premiumType: Int,
    @SerializedName("premium_usage_flags")
    val premiumUsageFlags: Int,
    @SerializedName("pronouns")
    val pronouns: String,
    @SerializedName("purchased_flags")
    val purchasedFlags: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("verified")
    val verified: Boolean
)