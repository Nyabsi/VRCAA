package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class RequiredTwoFactorType(
    @SerializedName("requiresTwoFactorAuth")
    val requiresTwoFactorAuth: List<String>?
)