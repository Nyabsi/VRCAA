package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import com.google.gson.annotations.SerializedName

data class NotificationV2Delete(
    @SerializedName("ids")
    var ids: List<String> = listOf(),
    @SerializedName("version")
    var version: Int = 0
)