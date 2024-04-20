package cc.sovellus.vrcaa.api.discord.models


import com.google.gson.annotations.SerializedName

data class DiscordTicket(
    @SerializedName("backup")
    val backup: Boolean,
    @SerializedName("mfa")
    val mfa: Boolean,
    @SerializedName("sms")
    val sms: Boolean,
    @SerializedName("ticket")
    val ticket: String,
    @SerializedName("totp")
    val totp: Boolean,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("webauthn")
    val webauthn: Any?
)