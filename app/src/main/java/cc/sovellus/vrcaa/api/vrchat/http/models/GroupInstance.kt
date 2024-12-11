package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class GroupInstance(
    @SerializedName("instanceId")
    val instanceId: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("memberCount")
    val memberCount: Int,
    @SerializedName("world")
    val world: World
)