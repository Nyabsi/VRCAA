package cc.sovellus.vrcaa.api.discord.websocket.models

import com.google.gson.annotations.SerializedName

data class Hello(
    @SerializedName("heartbeat_interval")
    val heartbeatInterval: Long,
    @SerializedName("trace")
    val trace: List<String>?
)