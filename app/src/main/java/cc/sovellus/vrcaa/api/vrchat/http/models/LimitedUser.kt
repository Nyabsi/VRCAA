package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class LimitedUser(
    @SerializedName("ageVerificationStatus")
    var ageVerificationStatus: String = "",
    @SerializedName("ageVerified")
    var ageVerified: Boolean = false,
    @SerializedName("allowAvatarCopying")
    var allowAvatarCopying: Boolean = false,
    @SerializedName("bannerType")
    var bannerType: String = "",
    @SerializedName("bannerUrl")
    var bannerUrl: String = "",
    @SerializedName("date_joined")
    var dateJoined: String = "",
    @SerializedName("developerType")
    var developerType: String = "",
    @SerializedName("discordId")
    var discordId: String = "",
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("friendKey")
    var friendKey: String = "",
    @SerializedName("friendRequestStatus")
    var friendRequestStatus: String = "",
    @SerializedName("iconFrame")
    var iconFrame: String = "",
    @SerializedName("iconUrl")
    var iconUrl: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("instanceId")
    var instanceId: String = "",
    @SerializedName("isEconomyCreator")
    var isEconomyCreator: Boolean = false,
    @SerializedName("isFriend")
    var isFriend: Boolean = false,
    @SerializedName("last_activity")
    var lastActivity: String = "",
    @SerializedName("last_login")
    var lastLogin: String = "",
    @SerializedName("last_mobile")
    var lastMobile: String = "",
    @SerializedName("last_platform")
    var lastPlatform: String = "",
    @SerializedName("location")
    var location: String = "",
    @SerializedName("nameplateEffect")
    var nameplateEffect: String = "",
    @SerializedName("note")
    var note: String = "",
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("profileEffect")
    var profileEffect: String = "",
    @SerializedName("pronouns")
    var pronouns: String = "",
    @SerializedName("state")
    var state: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("statusDescription")
    var statusDescription: String = "",
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("travelingToInstance")
    var travelingToInstance: String = "",
    @SerializedName("travelingToLocation")
    var travelingToLocation: String = "",
    @SerializedName("travelingToWorld")
    var travelingToWorld: String = "",
    @SerializedName("worldId")
    var worldId: String = ""
)