package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("activities")
    val activities: List<Any>,
    @SerializedName("client_info")
    val clientInfo: ClientInfo,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("status")
    val status: String
) {
    data class ClientInfo(
        @SerializedName("client")
        val client: String,
        @SerializedName("os")
        val os: String,
        @SerializedName("version")
        val version: Int
    )
}