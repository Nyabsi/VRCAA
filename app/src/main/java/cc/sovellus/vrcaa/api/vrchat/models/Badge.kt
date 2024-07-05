package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class Badge(
    @SerializedName("badgeDescription")
    val badgeDescription: String,
    @SerializedName("badgeId")
    val badgeId: String,
    @SerializedName("badgeImageUrl")
    val badgeImageUrl: String,
    @SerializedName("badgeName")
    val badgeName: String,
    @SerializedName("showcased")
    val showcased: Boolean
)