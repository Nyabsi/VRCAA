package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class UnityPackageXX(
    @SerializedName("assetUrl")
    var assetUrl: String = "",
    @SerializedName("assetUrlObject")
    var assetUrlObject: AssetUrlObject? = AssetUrlObject(),
    @SerializedName("assetVersion")
    var assetVersion: Int = 0,
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("pluginUrl")
    var pluginUrl: String? = "",
    @SerializedName("pluginUrlObject")
    var pluginUrlObject: PluginUrlObject? = PluginUrlObject(),
    @SerializedName("unitySortNumber")
    var unitySortNumber: Long = 0,
    @SerializedName("unityVersion")
    var unityVersion: String = ""
)