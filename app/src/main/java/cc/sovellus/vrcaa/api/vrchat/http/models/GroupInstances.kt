package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

class GroupInstances : ArrayList<GroupInstances.GroupInstance>(){
    data class GroupInstance(
        @SerializedName("instanceId")
        val instanceId: String,
        @SerializedName("location")
        val location: String,
        @SerializedName("memberCount")
        val memberCount: Int,
        @SerializedName("world")
        val world: World
    ) {
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
            val previewYoutubeId: Any?,
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
            val unityPackages: List<Any>,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("version")
            val version: Int,
            @SerializedName("visits")
            val visits: Int
        )
    }
}