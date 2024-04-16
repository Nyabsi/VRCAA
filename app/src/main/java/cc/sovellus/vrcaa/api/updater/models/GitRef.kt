package cc.sovellus.vrcaa.api.updater.models


import com.google.gson.annotations.SerializedName

data class GitRef(
    @SerializedName("node_id")
    val nodeId: String,
    @SerializedName("object")
    val objectX: Object,
    @SerializedName("ref")
    val ref: String,
    @SerializedName("url")
    val url: String
) {
    data class Object(
        @SerializedName("sha")
        val sha: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("url")
        val url: String
    )
}