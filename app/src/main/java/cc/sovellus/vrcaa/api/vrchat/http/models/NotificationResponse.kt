package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("responseData")
    var responseData: String = "",
    @SerializedName("responseType")
    var responseType: String = ""
)