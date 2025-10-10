package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Success(
    @SerializedName("success")
    var success: Success = Success()
) {
    data class Success(
        @SerializedName("message")
        var message: String = "",
        @SerializedName("status_code")
        var statusCode: Int = 0
    )
}