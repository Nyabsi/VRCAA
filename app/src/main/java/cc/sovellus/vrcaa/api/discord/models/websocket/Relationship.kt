package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Relationship(
    @SerializedName("id")
    val id: String,
    @SerializedName("nickname")
    val nickname: Any?,
    @SerializedName("since")
    val since: String?,
    @SerializedName("type")
    val type: Int,
    @SerializedName("user_id")
    val userId: String
)