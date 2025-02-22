package cc.sovellus.vrcaa.api.vrchat.http.models


import cc.sovellus.vrcaa.api.vrchat.models.UnityPackage
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
    var previewYoutubeId: Any? = Any(),
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
    @SerializedName("version")
    var version: Long = 0,
    @SerializedName("visits")
    var visits: Int = 0
)