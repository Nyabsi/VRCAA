package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class Friend(
    @SerializedName("bio")
    var bio: String = "",
    @SerializedName("bioLinks")
    var bioLinks: List<String>? = listOf(),
    @SerializedName("currentAvatarImageUrl")
    var currentAvatarImageUrl: String = "",
    @SerializedName("currentAvatarTags")
    var currentAvatarTags: List<String> = listOf(),
    @SerializedName("currentAvatarThumbnailImageUrl")
    var currentAvatarThumbnailImageUrl: String = "",
    @SerializedName("developerType")
    var developerType: String = "",
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("friendKey")
    var friendKey: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("isFriend")
    var isFriend: Boolean = false,
    @SerializedName("last_login")
    var lastLogin: String = "",
    @SerializedName("last_mobile")
    var lastMobile: Any? = Any(),
    @SerializedName("last_platform")
    var lastPlatform: String = "",
    @SerializedName("location")
    var location: String = "",
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("profilePicOverride")
    var profilePicOverride: String = "",
    @SerializedName("profilePicOverrideThumbnail")
    var profilePicOverrideThumbnail: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("statusDescription")
    var statusDescription: String = "",
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("userIcon")
    var userIcon: String = "",
    @Transient
    var isFavorite: Boolean = false
)