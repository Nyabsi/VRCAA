package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Presence(
    @SerializedName("avatarThumbnail")
    var avatarThumbnail: String = "",
    @SerializedName("currentAvatarTags")
    var currentAvatarTags: String = "",
    @SerializedName("debugflag")
    var debugflag: String = "",
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("groups")
    var groups: List<String> = listOf(),
    @SerializedName("id")
    var id: String = "",
    @SerializedName("instance")
    var instance: String = "",
    @SerializedName("instanceType")
    var instanceType: String = "",
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("profilePicOverride")
    var profilePicOverride: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("travelingToInstance")
    var travelingToInstance: String = "",
    @SerializedName("travelingToWorld")
    var travelingToWorld: String = "",
    @SerializedName("userIcon")
    var userIcon: String = "",
    @SerializedName("world")
    var world: String = ""
)