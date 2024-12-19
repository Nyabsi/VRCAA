package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class ProfileUpdate(
    @SerializedName("ageVerificationStatus")
    var ageVerificationStatus: String? = "",
    @SerializedName("bio")
    var bio: String = "",
    @SerializedName("bioLinks")
    var bioLinks: List<Any> = listOf(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("statusDescription")
    var statusDescription: String = ""
)