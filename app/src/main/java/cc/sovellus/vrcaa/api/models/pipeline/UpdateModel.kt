package cc.sovellus.vrcaa.api.models.pipeline


import com.google.gson.annotations.SerializedName

data class UpdateModel(
    @SerializedName("content")
    val content: String,
    @SerializedName("type")
    val type: String
)