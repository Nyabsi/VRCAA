package cc.sovellus.vrcaa.api.models


import com.google.gson.annotations.SerializedName

class Friends : ArrayList<Friends.FriendsItem>(){
    data class FriendsItem(
        @SerializedName("bio")
        val bio: String,
        @SerializedName("bioLinks")
        val bioLinks: List<String>?,
        @SerializedName("currentAvatarImageUrl")
        val currentAvatarImageUrl: String,
        @SerializedName("currentAvatarTags")
        val currentAvatarTags: List<String>,
        @SerializedName("currentAvatarThumbnailImageUrl")
        val currentAvatarThumbnailImageUrl: String,
        @SerializedName("developerType")
        val developerType: String,
        @SerializedName("displayName")
        val displayName: String,
        @SerializedName("friendKey")
        val friendKey: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("isFriend")
        val isFriend: Boolean,
        @SerializedName("last_login")
        val lastLogin: String,
        @SerializedName("last_platform")
        val lastPlatform: String,
        @SerializedName("location")
        var location: String,
        @SerializedName("profilePicOverride")
        val profilePicOverride: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("statusDescription")
        val statusDescription: String,
        @SerializedName("tags")
        val tags: List<String>,
        @SerializedName("userIcon")
        val userIcon: String
    )
}