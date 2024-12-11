package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Code(
    @SerializedName("code")
    val code: String
)