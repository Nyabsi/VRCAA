package cc.sovellus.vrcaa.api.models.pipeline


import com.google.gson.annotations.SerializedName

data class FriendLocation(
    @SerializedName("canRequestInvite")
    val canRequestInvite: Boolean,
    @SerializedName("location")
    val location: String,
    @SerializedName("travelingToLocation")
    val travelingToLocation: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("world")
    val world: World,
    @SerializedName("worldId")
    val worldId: String
) {
    data class User(
        @SerializedName("allowAvatarCopying")
        val allowAvatarCopying: Boolean,
        @SerializedName("bio")
        val bio: String,
        @SerializedName("bioLinks")
        val bioLinks: List<Any>,
        @SerializedName("currentAvatarImageUrl")
        val currentAvatarImageUrl: String,
        @SerializedName("currentAvatarTags")
        val currentAvatarTags: List<Any>,
        @SerializedName("currentAvatarThumbnailImageUrl")
        val currentAvatarThumbnailImageUrl: String,
        @SerializedName("date_joined")
        val dateJoined: String,
        @SerializedName("developerType")
        val developerType: String,
        @SerializedName("displayName")
        val displayName: String,
        @SerializedName("friendKey")
        val friendKey: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("isFriend")
        val isFriend: Boolean,
        @SerializedName("last_activity")
        val lastActivity: String,
        @SerializedName("last_login")
        val lastLogin: String,
        @SerializedName("last_platform")
        val lastPlatform: String,
        @SerializedName("profilePicOverride")
        val profilePicOverride: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("statusDescription")
        val statusDescription: String,
        @SerializedName("tags")
        val tags: List<String>,
        @SerializedName("userIcon")
        val userIcon: String
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
        @SerializedName("instances")
        val instances: List<Any>,
        @SerializedName("labsPublicationDate")
        val labsPublicationDate: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("namespace")
        val namespace: String,
        @SerializedName("occupants")
        val occupants: Int,
        @SerializedName("organization")
        val organization: String,
        @SerializedName("popularity")
        val popularity: Int,
        @SerializedName("previewYoutubeId")
        val previewYoutubeId: Any?,
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
        val udonProducts: List<Any>,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("version")
        val version: Int,
        @SerializedName("visits")
        val visits: Int
    )
}