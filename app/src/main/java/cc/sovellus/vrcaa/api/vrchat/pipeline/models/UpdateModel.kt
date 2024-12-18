package cc.sovellus.vrcaa.api.vrchat.pipeline.models


import com.google.gson.annotations.SerializedName

data class UpdateModel(
    @SerializedName("content")
    val content: String,
    @SerializedName("type")
    val type: String
)