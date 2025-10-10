package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class FriendStatus(
    @SerializedName("incomingRequest")
    var incomingRequest: Boolean = false,
    @SerializedName("isFriend")
    var isFriend: Boolean = false,
    @SerializedName("outgoingRequest")
    var outgoingRequest: Boolean = false
)