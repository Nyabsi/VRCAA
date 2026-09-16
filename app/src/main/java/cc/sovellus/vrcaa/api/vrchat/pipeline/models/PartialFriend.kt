package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import com.google.gson.annotations.SerializedName

data class PartialFriend(
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
    var dateJoined: String = "",
    @SerializedName("developerType")
    var developerType: String = "",
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("friendKey")
    var friendKey: String = "",
    @SerializedName("iconFrame")
    var iconFrame: String = "",
    @SerializedName("iconUrl")
    var iconUrl: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("isEconomyCreator")
    var isEconomyCreator: Boolean = false,
    @SerializedName("isFriend")
    var isFriend: Boolean = false,
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
    var tags: List<String> = listOf()
)