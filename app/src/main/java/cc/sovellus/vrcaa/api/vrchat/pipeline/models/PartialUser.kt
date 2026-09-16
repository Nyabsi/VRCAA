package cc.sovellus.vrcaa.api.vrchat.pipeline.models

import com.google.gson.annotations.SerializedName

data class PartialUser(
    @SerializedName("acceptedPrivacyVersion")
    var acceptedPrivacyVersion: Int = 0,
    @SerializedName("acceptedTOSVersion")
    var acceptedTOSVersion: Int = 0,
    @SerializedName("accountDeletionDate")
    var accountDeletionDate: Any = Any(),
    @SerializedName("accountDeletionLog")
    var accountDeletionLog: Any = Any(),
    @SerializedName("ageVerificationStatus")
    var ageVerificationStatus: String = "",
    @SerializedName("ageVerified")
    var ageVerified: Boolean = false,
    @SerializedName("allowAvatarCopying")
    var allowAvatarCopying: Boolean = false,
    @SerializedName("appleDetails")
    var appleDetails: AppleDetails = AppleDetails(),
    @SerializedName("appleId")
    var appleId: String = "",
    @SerializedName("bannerType")
    var bannerType: String = "",
    @SerializedName("bannerUrl")
    var bannerUrl: String = "",
    @SerializedName("completedTutorials")
    var completedTutorials: List<String> = listOf(),
    @SerializedName("contentFilters")
    var contentFilters: List<Any> = listOf(),
    @SerializedName("currentAvatar")
    var currentAvatar: String = "",
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
    @SerializedName("discordDetails")
    var discordDetails: DiscordDetails = DiscordDetails(),
    @SerializedName("discordId")
    var discordId: String = "",
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
    @SerializedName("googleDetails")
    var googleDetails: GoogleDetails = GoogleDetails(),
    @SerializedName("googleId")
    var googleId: String = "",
    @SerializedName("hasBirthday")
    var hasBirthday: Boolean = false,
    @SerializedName("hasDiscordFriendsOptOut")
    var hasDiscordFriendsOptOut: Boolean = false,
    @SerializedName("hasEmail")
    var hasEmail: Boolean = false,
    @SerializedName("hasLoggedInFromClient")
    var hasLoggedInFromClient: Boolean = false,
    @SerializedName("hasPendingEmail")
    var hasPendingEmail: Boolean = false,
    @SerializedName("hasSharedConnectionsOptOut")
    var hasSharedConnectionsOptOut: Boolean = false,
    @SerializedName("hideContentFilterSettings")
    var hideContentFilterSettings: Boolean = false,
    @SerializedName("homeLocation")
    var homeLocation: String = "",
    @SerializedName("iconFrame")
    var iconFrame: String = "",
    @SerializedName("iconUrl")
    var iconUrl: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("isAdult")
    var isAdult: Boolean = false,
    @SerializedName("isBoopingEnabled")
    var isBoopingEnabled: Boolean = false,
    @SerializedName("isEconomyCreator")
    var isEconomyCreator: Boolean = false,
    @SerializedName("isFriend")
    var isFriend: Boolean = false,
    @SerializedName("isTemporary")
    var isTemporary: Boolean = false,
    @SerializedName("last_activity")
    var lastActivity: String = "",
    @SerializedName("last_login")
    var lastLogin: String = "",
    @SerializedName("last_mobile")
    var lastMobile: Any = Any(),
    @SerializedName("last_platform")
    var lastPlatform: String = "",
    @SerializedName("nameplateEffect")
    var nameplateEffect: String = "",
    @SerializedName("obfuscatedEmail")
    var obfuscatedEmail: String = "",
    @SerializedName("obfuscatedPendingEmail")
    var obfuscatedPendingEmail: String = "",
    @SerializedName("oculusId")
    var oculusId: String = "",
    @SerializedName("pastDisplayNames")
    var pastDisplayNames: List<PastDisplayName> = listOf(),
    @SerializedName("personalizationOptOut")
    var personalizationOptOut: Boolean = false,
    @SerializedName("picoId")
    var picoId: String = "",
    @SerializedName("platform_history")
    var platformHistory: List<Any> = listOf(),
    @SerializedName("profileEffect")
    var profileEffect: String = "",
    @SerializedName("pronouns")
    var pronouns: String = "",
    @SerializedName("pronounsHistory")
    var pronounsHistory: List<String> = listOf(),
    @SerializedName("queuedInstance")
    var queuedInstance: Any = Any(),
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
    @SerializedName("temporaryExpiryDate")
    var temporaryExpiryDate: Any = Any(),
    @SerializedName("twitchDetails")
    var twitchDetails: TwitchDetails = TwitchDetails(),
    @SerializedName("twitchId")
    var twitchId: String = "",
    @SerializedName("twoFactorAuthEnabled")
    var twoFactorAuthEnabled: Boolean = false,
    @SerializedName("twoFactorAuthEnabledDate")
    var twoFactorAuthEnabledDate: Any = Any(),
    @SerializedName("unsubscribe")
    var unsubscribe: Boolean = false,
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("userLanguage")
    var userLanguage: String = "",
    @SerializedName("userLanguageCode")
    var userLanguageCode: String = "",
    @SerializedName("username")
    var username: String = "",
    @SerializedName("usesGeneratedPassword")
    var usesGeneratedPassword: Boolean = false,
    @SerializedName("viveId")
    var viveId: String = ""
) {
    class AppleDetails

    class DiscordDetails

    class GoogleDetails

    data class PastDisplayName(
        @SerializedName("displayName")
        var displayName: String = "",
        @SerializedName("reverted")
        var reverted: Boolean = false,
        @SerializedName("updated_at")
        var updatedAt: String = ""
    )

    class SteamDetails

    class TwitchDetails
}