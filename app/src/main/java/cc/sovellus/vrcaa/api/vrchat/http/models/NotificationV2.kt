package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class NotificationV2(
    @SerializedName("canDelete")
    var canDelete: Boolean = false,
    @SerializedName("category")
    var category: String = "",
    @SerializedName("createdAt")
    var createdAt: String = "",
    @SerializedName("data")
    var `data`: Any = Any(),
    @SerializedName("expiresAt")
    var expiresAt: String = "",
    @SerializedName("expiryAfterSeen")
    var expiryAfterSeen: Int = 0,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("ignoreDND")
    var ignoreDND: Boolean = false,
    @SerializedName("imageUrl")
    var imageUrl: String?,
    @SerializedName("isSystem")
    var isSystem: Boolean = false,
    @SerializedName("link")
    var link: String?,
    @SerializedName("linkText")
    var linkText: String?,
    @SerializedName("linkTextKey")
    var linkTextKey: String?,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("messageKey")
    var messageKey: String?,
    @SerializedName("receiverUserId")
    var receiverUserId: String = "",
    @SerializedName("relatedNotificationsId")
    var relatedNotificationsId: String = "",
    @SerializedName("requireSeen")
    var requireSeen: Boolean = false,
    @SerializedName("responses")
    var responses: List<Response> = listOf(),
    @SerializedName("seen")
    var seen: Boolean = false,
    @SerializedName("senderUserId")
    var senderUserId: String?,
    @SerializedName("senderUsername")
    var senderUsername: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("titleKey")
    var titleKey: String?,
    @SerializedName("type")
    var type: String = "",
    @SerializedName("updatedAt")
    var updatedAt: String = "",
    @SerializedName("version")
    var version: Int = 0
) {
    data class Response(
        @SerializedName("data")
        var `data`: String = "",
        @SerializedName("icon")
        var icon: String = "",
        @SerializedName("text")
        var text: String = "",
        @SerializedName("textKey")
        var textKey: String = "",
        @SerializedName("type")
        var type: String = ""
    )
}