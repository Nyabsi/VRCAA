package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class UserNoteUpdate(
    @SerializedName("targetUserId")
    var targetUserId: String = "",
    @SerializedName("note")
    var note: String = ""
)