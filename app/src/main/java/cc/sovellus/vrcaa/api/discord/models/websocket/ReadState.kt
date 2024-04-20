package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class ReadState(
    @SerializedName("entries")
    val entries: List<Entry>,
    @SerializedName("partial")
    val partial: Boolean,
    @SerializedName("version")
    val version: Int
) {
    data class Entry(
        @SerializedName("badge_count")
        val badgeCount: Int?,
        @SerializedName("flags")
        val flags: Int?,
        @SerializedName("id")
        val id: String,
        @SerializedName("last_acked_id")
        val lastAckedId: String?,
        @SerializedName("last_message_id")
        val lastMessageId: String?,
        @SerializedName("last_pin_timestamp")
        val lastPinTimestamp: String?,
        @SerializedName("last_viewed")
        val lastViewed: Int?,
        @SerializedName("mention_count")
        val mentionCount: Int?,
        @SerializedName("read_state_type")
        val readStateType: Int?
    )
}