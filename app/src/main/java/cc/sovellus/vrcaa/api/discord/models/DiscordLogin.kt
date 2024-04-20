package cc.sovellus.vrcaa.api.discord.models


import com.google.gson.annotations.SerializedName

data class DiscordLogin(
    @SerializedName("token")
    val token: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_settings")
    val userSettings: UserSettings
) {
    data class UserSettings(
        @SerializedName("locale")
        val locale: String,
        @SerializedName("theme")
        val theme: String
    )
}