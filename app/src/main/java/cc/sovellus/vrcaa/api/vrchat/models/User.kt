package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("acceptedPrivacyVersion")
    val acceptedPrivacyVersion: Int,
    @SerializedName("acceptedTOSVersion")
    val acceptedTOSVersion: Int,
    @SerializedName("accountDeletionDate")
    val accountDeletionDate: Any?,
    @SerializedName("accountDeletionLog")
    val accountDeletionLog: Any?,
    @SerializedName("activeFriends")
    val activeFriends: List<String>,
    @SerializedName("allowAvatarCopying")
    val allowAvatarCopying: Boolean,
    @SerializedName("bio")
    val bio: String,
    @SerializedName("bioLinks")
    val bioLinks: List<String>,
    @SerializedName("currentAvatar")
    val currentAvatar: String,
    @SerializedName("currentAvatarAssetUrl")
    val currentAvatarAssetUrl: String,
    @SerializedName("currentAvatarImageUrl")
    val currentAvatarImageUrl: String,
    @SerializedName("currentAvatarTags")
    val currentAvatarTags: List<Any>,
    @SerializedName("currentAvatarThumbnailImageUrl")
    val currentAvatarThumbnailImageUrl: String,
    @SerializedName("date_joined")
    val dateJoined: String,
    @SerializedName("developerType")
    val developerType: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("emailVerified")
    val emailVerified: Boolean,
    @SerializedName("fallbackAvatar")
    val fallbackAvatar: String,
    @SerializedName("friendGroupNames")
    val friendGroupNames: List<Any>,
    @SerializedName("friendKey")
    val friendKey: String,
    @SerializedName("friends")
    val friends: List<String>,
    @SerializedName("googleId")
    val googleId: String,
    @SerializedName("hasBirthday")
    val hasBirthday: Boolean,
    @SerializedName("hasEmail")
    val hasEmail: Boolean,
    @SerializedName("hasLoggedInFromClient")
    val hasLoggedInFromClient: Boolean,
    @SerializedName("hasPendingEmail")
    val hasPendingEmail: Boolean,
    @SerializedName("hideContentFilterSettings")
    val hideContentFilterSettings: Boolean,
    @SerializedName("homeLocation")
    val homeLocation: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isFriend")
    val isFriend: Boolean,
    @SerializedName("last_activity")
    val lastActivity: String,
    @SerializedName("last_login")
    val lastLogin: String,
    @SerializedName("last_platform")
    val lastPlatform: String,
    @SerializedName("obfuscatedEmail")
    val obfuscatedEmail: String,
    @SerializedName("obfuscatedPendingEmail")
    val obfuscatedPendingEmail: String,
    @SerializedName("oculusId")
    val oculusId: String,
    @SerializedName("offlineFriends")
    val offlineFriends: List<String>,
    @SerializedName("onlineFriends")
    val onlineFriends: List<String>,
    @SerializedName("pastDisplayNames")
    val pastDisplayNames: List<PastDisplayName>,
    @SerializedName("picoId")
    val picoId: String,
    @SerializedName("presence")
    val presence: Presence,
    @SerializedName("profilePicOverride")
    val profilePicOverride: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusDescription")
    val statusDescription: String,
    @SerializedName("statusFirstTime")
    val statusFirstTime: Boolean,
    @SerializedName("statusHistory")
    val statusHistory: List<String>,
    @SerializedName("steamDetails")
    val steamDetails: SteamDetails,
    @SerializedName("steamId")
    val steamId: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("twoFactorAuthEnabled")
    val twoFactorAuthEnabled: Boolean,
    @SerializedName("twoFactorAuthEnabledDate")
    val twoFactorAuthEnabledDate: Any?,
    @SerializedName("unsubscribe")
    val unsubscribe: Boolean,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("userIcon")
    val userIcon: String,
    @SerializedName("userLanguage")
    val userLanguage: String,
    @SerializedName("userLanguageCode")
    val userLanguageCode: Any?,
    @SerializedName("username")
    val username: String,
    @SerializedName("viveId")
    val viveId: String
) {
    data class PastDisplayName(
        @SerializedName("displayName")
        val displayName: String,
        @SerializedName("reverted")
        val reverted: Boolean,
        @SerializedName("updated_at")
        val updatedAt: String
    )

    data class Presence(
        @SerializedName("groups")
        val groups: List<Any>,
        @SerializedName("id")
        val id: String,
        @SerializedName("instance")
        val instance: String,
        @SerializedName("instanceType")
        val instanceType: String,
        @SerializedName("platform")
        val platform: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("travelingToInstance")
        val travelingToInstance: String,
        @SerializedName("travelingToWorld")
        val travelingToWorld: String,
        @SerializedName("world")
        val world: String
    )

    class SteamDetails
}