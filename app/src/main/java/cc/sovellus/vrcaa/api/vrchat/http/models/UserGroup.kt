package cc.sovellus.vrcaa.api.vrchat.http.models

import com.google.gson.annotations.SerializedName

data class UserGroup(
    @SerializedName("bannerId")
    val bannerId: String,
    @SerializedName("bannerUrl")
    val bannerUrl: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discriminator")
    val discriminator: String,
    @SerializedName("groupId")
    val groupId: String,
    @SerializedName("iconId")
    val iconId: String?,
    @SerializedName("iconUrl")
    val iconUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isRepresenting")
    val isRepresenting: Boolean?,
    @SerializedName("memberCount")
    val memberCount: Int,
    @SerializedName("memberVisibility")
    val memberVisibility: String,
    @SerializedName("mutualGroup")
    val mutualGroup: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("privacy")
    val privacy: String,
    @SerializedName("shortCode")
    val shortCode: String
)