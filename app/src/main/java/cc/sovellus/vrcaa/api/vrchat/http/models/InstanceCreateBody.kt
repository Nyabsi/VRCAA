package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class InstanceCreateBody(
    @SerializedName("canRequestInvite")
    var canRequestInvite: Boolean = false,
    @SerializedName("ownerId")
    var ownerId: String? = "",
    @SerializedName("region")
    var region: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("worldId")
    var worldId: String = ""
)