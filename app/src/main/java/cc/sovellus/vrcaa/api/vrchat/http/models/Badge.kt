package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Badge(
    @SerializedName("badgeDescription")
    var badgeDescription: String = "",
    @SerializedName("badgeId")
    var badgeId: String = "",
    @SerializedName("badgeImageUrl")
    var badgeImageUrl: String = "",
    @SerializedName("badgeName")
    var badgeName: String = "",
    @SerializedName("showcased")
    var showcased: Boolean = false
)