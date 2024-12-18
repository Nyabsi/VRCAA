package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("details")
    val details: Details,
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("receiverUserId")
    val receiverUserId: String,
    @SerializedName("senderUserId")
    val senderUserId: String,
    @SerializedName("senderUsername")
    val senderUsername: String,
    @SerializedName("type")
    val type: String
) {
    class Details
}