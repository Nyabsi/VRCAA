package cc.sovellus.vrcaa.api.search.avtrdb.models


import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("vrc_id")
    var vrcId: String = ""
)