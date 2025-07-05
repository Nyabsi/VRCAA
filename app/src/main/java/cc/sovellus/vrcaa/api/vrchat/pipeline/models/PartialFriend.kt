package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import com.google.gson.annotations.SerializedName

data class PartialFriend(
    @SerializedName("ageVerificationStatus")
    var ageVerificationStatus: String,
    @SerializedName("ageVerified")
    var ageVerified: Boolean,
    @SerializedName("allowAvatarCopying")
    var allowAvatarCopying: Boolean,
    @SerializedName("bio")
    var bio: String,
    @SerializedName("bioLinks")
    var bioLinks: List<String>,
    @SerializedName("currentAvatarImageUrl")
    var currentAvatarImageUrl: String,
    @SerializedName("currentAvatarTags")
    var currentAvatarTags: List<String>,
    @SerializedName("currentAvatarThumbnailImageUrl")
    var currentAvatarThumbnailImageUrl: String,
    @SerializedName("date_joined")
    var dateJoined: String,
    @SerializedName("developerType")
    var developerType: String,
    @SerializedName("displayName")
    var displayName: String,
    @SerializedName("friendKey")
    var friendKey: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("isFriend")
    var isFriend: Boolean,
    @SerializedName("last_activity")
    var lastActivity: String,
    @SerializedName("last_login")
    var lastLogin: String,
    @SerializedName("last_mobile")
    var lastMobile: Any,
    @SerializedName("last_platform")
    var lastPlatform: String,
    @SerializedName("profilePicOverride")
    var profilePicOverride: String,
    @SerializedName("profilePicOverrideThumbnail")
    var profilePicOverrideThumbnail: String,
    @SerializedName("pronouns")
    var pronouns: String,
    @SerializedName("state")
    var state: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("statusDescription")
    var statusDescription: String,
    @SerializedName("tags")
    var tags: List<String>,
    @SerializedName("userIcon")
    var userIcon: String
)