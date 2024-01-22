package cc.sovellus.vrcaa.api.models


import com.google.gson.annotations.SerializedName

data class Instance(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("clientNumber")
    val clientNumber: String,
    @SerializedName("closedAt")
    val closedAt: Any?,
    @SerializedName("displayName")
    val displayName: Any?,
    @SerializedName("full")
    val full: Boolean,
    @SerializedName("gameServerVersion")
    val gameServerVersion: Int,
    @SerializedName("hardClose")
    val hardClose: Any?,
    @SerializedName("hasCapacityForYou")
    val hasCapacityForYou: Boolean,
    @SerializedName("hidden")
    val hidden: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("instanceId")
    val instanceId: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("n_users")
    val nUsers: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("permanent")
    val permanent: Boolean,
    @SerializedName("photonRegion")
    val photonRegion: String,
    @SerializedName("platforms")
    val platforms: Platforms,
    @SerializedName("queueEnabled")
    val queueEnabled: Boolean,
    @SerializedName("recommendedCapacity")
    val recommendedCapacity: Int,
    @SerializedName("region")
    val region: String,
    @SerializedName("secureName")
    val secureName: String,
    @SerializedName("shortName")
    val shortName: Any?,
    @SerializedName("strict")
    val strict: Boolean,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("type")
    val type: String,
    @SerializedName("userCount")
    val userCount: Int,
    @SerializedName("world")
    val world: World,
    @SerializedName("worldId")
    val worldId: String
) {
    data class Platforms(
        @SerializedName("android")
        val android: Int,
        @SerializedName("standalonewindows")
        val standalonewindows: Int
    )

    data class World(
        @SerializedName("authorId")
        val authorId: String,
        @SerializedName("authorName")
        val authorName: String,
        @SerializedName("capacity")
        val capacity: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("favorites")
        val favorites: Int,
        @SerializedName("featured")
        val featured: Boolean,
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
        @SerializedName("namespace")
        val namespace: String,
        @SerializedName("organization")
        val organization: String,
        @SerializedName("popularity")
        val popularity: Int,
        @SerializedName("previewYoutubeId")
        val previewYoutubeId: String,
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
        val udonProducts: List<Any>,
        @SerializedName("unityPackages")
        val unityPackages: List<UnityPackage>,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("version")
        val version: Int,
        @SerializedName("visits")
        val visits: Int
    ) {
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
}