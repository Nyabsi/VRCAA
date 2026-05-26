package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class NoteResponse(
    @SerializedName("createdAt")
    var createdAt: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("note")
    var note: String = "",
    @SerializedName("targetUserId")
    var targetUserId: String = "",
    @SerializedName("userId")
    var userId: String = ""
)