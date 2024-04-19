package cc.sovellus.vrcaa.api.discord.websocket.models


import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class Incoming(
    @SerializedName("op")
    val op: Int,
    @SerializedName("d")
    val d: JsonElement,
    @SerializedName("s")
    val s: Int?,
    @SerializedName("t")
    val t: String?
)