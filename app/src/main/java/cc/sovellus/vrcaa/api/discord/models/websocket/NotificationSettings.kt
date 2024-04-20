package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class NotificationSettings(
    @SerializedName("flags")
    val flags: Int
)