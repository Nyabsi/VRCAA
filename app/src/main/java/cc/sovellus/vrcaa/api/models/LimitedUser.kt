package cc.sovellus.vrcaa.api.models


import com.google.gson.annotations.SerializedName

data class LimitedUser(
    @SerializedName("bio")
    val bio: String,
    @SerializedName("currentAvatarImageUrl")
    val currentAvatarImageUrl: String,
    @SerializedName("currentAvatarThumbnailImageUrl")
    val currentAvatarThumbnailImageUrl: String,
    @SerializedName("developerType")
    val developerType: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("fallbackAvatar")
    val fallbackAvatar: String,
    @SerializedName("friendKey")
    val friendKey: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isFriend")
    val isFriend: Boolean,
    @SerializedName("last_platform")
    val lastPlatform: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("profilePicOverride")
    val profilePicOverride: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusDescription")
    val statusDescription: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("userIcon")
    val userIcon: String
)