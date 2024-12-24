package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("acceptedPrivacyVersion")
    var acceptedPrivacyVersion: Int = 0,
    @SerializedName("acceptedTOSVersion")
    var acceptedTOSVersion: Int = 0,
    @SerializedName("accountDeletionDate")
    var accountDeletionDate: Any? = Any(),
    @SerializedName("accountDeletionLog")
    var accountDeletionLog: Any? = Any(),
    @SerializedName("activeFriends")
    var activeFriends: List<String> = listOf(),
    @SerializedName("ageVerificationStatus")
    var ageVerificationStatus: String = "",
    @SerializedName("ageVerified")
    var ageVerified: Boolean = false,
    @SerializedName("allowAvatarCopying")
    var allowAvatarCopying: Boolean = false,
    @SerializedName("badges")
    var badges: List<Badge> = listOf(),
    @SerializedName("bio")
    var bio: String = "",
    @SerializedName("bioLinks")
    var bioLinks: List<String> = listOf(),
    @SerializedName("contentFilters")
    var contentFilters: List<Any> = listOf(),
    @SerializedName("currentAvatar")
    var currentAvatar: String = "",
    @SerializedName("currentAvatarAssetUrl")
    var currentAvatarAssetUrl: String = "",
    @SerializedName("currentAvatarImageUrl")
    var currentAvatarImageUrl: String = "",
    @SerializedName("currentAvatarTags")
    var currentAvatarTags: List<Any> = listOf(),
    @SerializedName("currentAvatarThumbnailImageUrl")
    var currentAvatarThumbnailImageUrl: String = "",
    @SerializedName("date_joined")
    var dateJoined: String = "",
    @SerializedName("developerType")
    var developerType: String = "",
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("emailVerified")
    var emailVerified: Boolean = false,
    @SerializedName("fallbackAvatar")
    var fallbackAvatar: String = "",
    @SerializedName("friendGroupNames")
    var friendGroupNames: List<Any> = listOf(),
    @SerializedName("friendKey")
    var friendKey: String = "",
    @SerializedName("friends")
    var friends: List<String> = listOf(),
    @SerializedName("googleDetails")
    var googleDetails: GoogleDetails = GoogleDetails(),
    @SerializedName("googleId")
    var googleId: String = "",
    @SerializedName("hasBirthday")
    var hasBirthday: Boolean = false,
    @SerializedName("hasEmail")
    var hasEmail: Boolean = false,
    @SerializedName("hasLoggedInFromClient")
    var hasLoggedInFromClient: Boolean = false,
    @SerializedName("hasPendingEmail")
    var hasPendingEmail: Boolean = false,
    @SerializedName("hideContentFilterSettings")
    var hideContentFilterSettings: Boolean = false,
    @SerializedName("homeLocation")
    var homeLocation: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("isAdult")
    var isAdult: Boolean = false,
    @SerializedName("isBoopingEnabled")
    var isBoopingEnabled: Boolean = false,
    @SerializedName("isFriend")
    var isFriend: Boolean = false,
    @SerializedName("last_activity")
    var lastActivity: String = "",
    @SerializedName("last_login")
    var lastLogin: String = "",
    @SerializedName("last_mobile")
    var lastMobile: Any? = Any(),
    @SerializedName("last_platform")
    var lastPlatform: String = "",
    @SerializedName("obfuscatedEmail")
    var obfuscatedEmail: String = "",
    @SerializedName("obfuscatedPendingEmail")
    var obfuscatedPendingEmail: String = "",
    @SerializedName("oculusId")
    var oculusId: String = "",
    @SerializedName("offlineFriends")
    var offlineFriends: List<String> = listOf(),
    @SerializedName("onlineFriends")
    var onlineFriends: List<String> = listOf(),
    @SerializedName("pastDisplayNames")
    var pastDisplayNames: List<Any> = listOf(),
    @SerializedName("picoId")
    var picoId: String = "",
    @SerializedName("presence")
    var presence: Presence = Presence(),
    @SerializedName("profilePicOverride")
    var profilePicOverride: String = "",
    @SerializedName("profilePicOverrideThumbnail")
    var profilePicOverrideThumbnail: String = "",
    @SerializedName("pronouns")
    var pronouns: String = "",
    @SerializedName("receiveMobileInvitations")
    var receiveMobileInvitations: Boolean = false,
    @SerializedName("state")
    var state: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("statusDescription")
    var statusDescription: String = "",
    @SerializedName("statusFirstTime")
    var statusFirstTime: Boolean = false,
    @SerializedName("statusHistory")
    var statusHistory: List<String> = listOf(),
    @SerializedName("steamDetails")
    var steamDetails: SteamDetails = SteamDetails(),
    @SerializedName("steamId")
    var steamId: String = "",
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("twoFactorAuthEnabled")
    var twoFactorAuthEnabled: Boolean = false,
    @SerializedName("twoFactorAuthEnabledDate")
    var twoFactorAuthEnabledDate: Any? = Any(),
    @SerializedName("unsubscribe")
    var unsubscribe: Boolean = false,
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("userIcon")
    var userIcon: String = "",
    @SerializedName("userLanguage")
    var userLanguage: Any? = Any(),
    @SerializedName("userLanguageCode")
    var userLanguageCode: String = "",
    @SerializedName("username")
    var username: String = "",
    @SerializedName("viveId")
    var viveId: String = ""
)