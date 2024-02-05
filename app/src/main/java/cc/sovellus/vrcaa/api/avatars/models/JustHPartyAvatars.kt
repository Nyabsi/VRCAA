package cc.sovellus.vrcaa.api.avatars.models


import com.google.gson.annotations.SerializedName

class JustHPartyAvatars : ArrayList<JustHPartyAvatars.JustHPartyAvatarsItem>(){
    data class JustHPartyAvatarsItem(
        @SerializedName("authorId")
        val authorId: String,
        @SerializedName("authorName")
        val authorName: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("imageUrl")
        val imageUrl: Any?,
        @SerializedName("name")
        val name: String,
        @SerializedName("releaseStatus")
        val releaseStatus: String,
        @SerializedName("thumbnailImageUrl")
        val thumbnailImageUrl: String
    )
}