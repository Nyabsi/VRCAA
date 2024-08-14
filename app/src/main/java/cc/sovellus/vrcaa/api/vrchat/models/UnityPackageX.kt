package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class UnityPackageX(
    @SerializedName("assetVersion")
    var assetVersion: Int = 0,
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("impostorUrl")
    var impostorUrl: Any? = Any(),
    @SerializedName("impostorizerVersion")
    var impostorizerVersion: String? = "",
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("scanStatus")
    var scanStatus: String = "",
    @SerializedName("unityVersion")
    var unityVersion: String = "",
    @SerializedName("variant")
    var variant: String? = ""
)