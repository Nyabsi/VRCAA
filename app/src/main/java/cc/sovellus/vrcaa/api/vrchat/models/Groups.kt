package cc.sovellus.vrcaa.api.vrchat.models

import com.google.gson.annotations.SerializedName

class Groups : ArrayList<Groups.Group>() {
    data class Group(
        @SerializedName("bannerId")
        val bannerId: String?,
        @SerializedName("bannerUrl")
        val bannerUrl: String,
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("discriminator")
        val discriminator: String,
        @SerializedName("galleries")
        val galleries: List<Any>,
        @SerializedName("iconId")
        val iconId: String?,
        @SerializedName("iconUrl")
        val iconUrl: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("isSearchable")
        val isSearchable: Boolean,
        @SerializedName("memberCount")
        val memberCount: Int,
        @SerializedName("membershipStatus")
        val membershipStatus: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("ownerId")
        val ownerId: String,
        @SerializedName("rules")
        val rules: String,
        @SerializedName("shortCode")
        val shortCode: String,
        @SerializedName("tags")
        val tags: List<Any>
    )
}