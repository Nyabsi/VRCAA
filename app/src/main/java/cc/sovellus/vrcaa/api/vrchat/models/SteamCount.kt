package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class SteamCount(
    @SerializedName("response")
    var response: Response = Response()
) {
    data class Response(
        @SerializedName("player_count")
        var playerCount: Int = 0,
        @SerializedName("result")
        var result: Int = 0
    )
}