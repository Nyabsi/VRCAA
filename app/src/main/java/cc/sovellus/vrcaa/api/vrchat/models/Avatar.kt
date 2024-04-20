package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("assetUrl")
    val assetUrl: String,
    @SerializedName("assetUrlObject")
    val assetUrlObject: AssetUrlObject,
    @SerializedName("authorId")
    val authorId: String,
    @SerializedName("authorName")
    val authorName: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("featured")
    val featured: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("releaseStatus")
    val releaseStatus: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("thumbnailImageUrl")
    val thumbnailImageUrl: String,
    @SerializedName("unityPackageUrl")
    val unityPackageUrl: String,
    @SerializedName("unityPackages")
    val unityPackages: List<UnityPackage>,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("version")
    val version: Int
) {
    class AssetUrlObject

    data class UnityPackage(
        @SerializedName("assetUrl")
        val assetUrl: String,
        @SerializedName("assetUrlObject")
        val assetUrlObject: AssetUrlObject?,
        @SerializedName("assetVersion")
        val assetVersion: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("platform")
        val platform: String,
        @SerializedName("pluginUrl")
        val pluginUrl: String,
        @SerializedName("pluginUrlObject")
        val pluginUrlObject: PluginUrlObject?,
        @SerializedName("unitySortNumber")
        val unitySortNumber: Long,
        @SerializedName("unityVersion")
        val unityVersion: String
    ) {
        class AssetUrlObject

        class PluginUrlObject
    }
}