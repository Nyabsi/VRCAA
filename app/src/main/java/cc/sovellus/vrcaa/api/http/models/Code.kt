package cc.sovellus.vrcaa.api.http.models


import com.google.gson.annotations.SerializedName

data class Code(
    @SerializedName("code")
    val code: String
)