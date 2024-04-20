package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class World(
    @SerializedName("authorId")
    val authorId: String,
    @SerializedName("authorName")
    val authorName: String,
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("featured")
    val featured: Boolean,
    @SerializedName("favorites")
    val favorites: Int,
    @SerializedName("heat")
    val heat: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("labsPublicationDate")
    val labsPublicationDate: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("occupants")
    val occupants: Int,
    @SerializedName("organization")
    val organization: String,
    @SerializedName("popularity")
    val popularity: Int,
    @SerializedName("privateOccupants")
    val privateOccupants: Int,
    @SerializedName("publicOccupants")
    val publicOccupants: Int,
    @SerializedName("publicationDate")
    val publicationDate: String,
    @SerializedName("recommendedCapacity")
    val recommendedCapacity: Int,
    @SerializedName("releaseStatus")
    val releaseStatus: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("thumbnailImageUrl")
    val thumbnailImageUrl: String,
    @SerializedName("udonProducts")
    val udonProducts: List<String>,
    @SerializedName("unityPackages")
    val unityPackages: List<UnityPackage>,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("version")
    val version: Int,
    @SerializedName("visits")
    val visits: Int,
    @SerializedName("instances")
    val instances: List<Array<Any>>
) {
    data class UnityPackage(
        @SerializedName("platform")
        val platform: String,
        @SerializedName("unityVersion")
        val unityVersion: String
    )
}