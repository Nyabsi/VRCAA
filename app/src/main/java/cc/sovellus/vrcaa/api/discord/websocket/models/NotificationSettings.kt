package cc.sovellus.vrcaa.api.discord.websocket.models

import com.google.gson.annotations.SerializedName

data class NotificationSettings(
    @SerializedName("flags")
    val flags: Int
)