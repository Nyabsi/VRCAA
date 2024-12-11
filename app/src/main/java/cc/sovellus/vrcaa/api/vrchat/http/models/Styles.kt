package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Styles(
    @SerializedName("primary")
    var primary: Any? = Any(),
    @SerializedName("secondary")
    var secondary: Any? = Any(),
    @SerializedName("supplementary")
    var supplementary: List<Any> = listOf()
)