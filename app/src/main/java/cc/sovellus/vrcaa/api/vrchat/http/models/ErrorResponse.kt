package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    var error: Error = Error()
)