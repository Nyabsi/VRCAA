package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("avatar_decoration_data")
    val avatarDecorationData: AvatarDecorationData?,
    @SerializedName("bot")
    val bot: Boolean?,
    @SerializedName("clan")
    val clan: Any?,
    @SerializedName("discriminator")
    val discriminator: String,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("global_name")
    val globalName: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("public_flags")
    val publicFlags: Int,
    @SerializedName("username")
    val username: String
) {
    data class AvatarDecorationData(
        @SerializedName("asset")
        val asset: String,
        @SerializedName("sku_id")
        val skuId: String
    )
}