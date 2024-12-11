package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("details")
    val details: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("seen")
    val seen: Boolean,
    @SerializedName("senderUserId")
    val senderUserId: String,
    @SerializedName("senderUsername")
    val senderUsername: String,
    @SerializedName("type")
    val type: String
)