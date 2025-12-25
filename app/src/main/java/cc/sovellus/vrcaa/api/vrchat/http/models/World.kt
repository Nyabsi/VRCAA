package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class World(
    @SerializedName("authorId")
    var authorId: String = "",
    @SerializedName("authorName")
    var authorName: String = "",
    @SerializedName("capacity")
    var capacity: Int = 0,
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("defaultContentSettings")
    var defaultContentSettings: DefaultContentSettings = DefaultContentSettings(),
    @SerializedName("description")
    var description: String = "",
    @SerializedName("favorites")
    var favorites: Int = 0,
    @SerializedName("featured")
    var featured: Boolean = false,
    @SerializedName("heat")
    var heat: Int = 0,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("instances")
    var instances: List<List<Any>> = listOf(),
    @SerializedName("labsPublicationDate")
    var labsPublicationDate: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("occupants")
    var occupants: Int = 0,
    @SerializedName("organization")
    var organization: String = "",
    @SerializedName("popularity")
    var popularity: Int = 0,
    @SerializedName("previewYoutubeId")
    var previewYoutubeId: Any = Any(),
    @SerializedName("privateOccupants")
    var privateOccupants: Int = 0,
    @SerializedName("publicOccupants")
    var publicOccupants: Int = 0,
    @SerializedName("publicationDate")
    var publicationDate: String = "",
    @SerializedName("recommendedCapacity")
    var recommendedCapacity: Int = 0,
    @SerializedName("releaseStatus")
    var releaseStatus: String = "",
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("thumbnailImageUrl")
    var thumbnailImageUrl: String = "",
    @SerializedName("udonProducts")
    var udonProducts: List<Any> = listOf(),
    @SerializedName("unityPackages")
    var unityPackages: List<UnityPackage> = listOf(),
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("urlList")
    var urlList: List<String> = listOf(),
    @SerializedName("version")
    var version: Int = 0,
    @SerializedName("visits")
    var visits: Int = 0
) {
    class DefaultContentSettings

    data class UnityPackage(
        @SerializedName("assetUrl")
        var assetUrl: String = "",
        @SerializedName("assetVersion")
        var assetVersion: Int = 0,
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("id")
        var id: String = "",
        @SerializedName("platform")
        var platform: String = "",
        @SerializedName("pluginUrl")
        var pluginUrl: Any = Any(),
        @SerializedName("scanStatus")
        var scanStatus: String = "",
        @SerializedName("unitySortNumber")
        var unitySortNumber: Long = 0,
        @SerializedName("unityVersion")
        var unityVersion: String = "",
        @SerializedName("variant")
        var variant: String = "",
        @SerializedName("worldSignature")
        var worldSignature: String = ""
    )
}