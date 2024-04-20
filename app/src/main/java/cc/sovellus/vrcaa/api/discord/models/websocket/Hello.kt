package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Hello(
    @SerializedName("heartbeat_interval")
    val heartbeatInterval: Long,
    @SerializedName("trace")
    val trace: List<String>?
)