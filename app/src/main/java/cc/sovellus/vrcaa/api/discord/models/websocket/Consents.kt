package cc.sovellus.vrcaa.api.discord.models.websocket

import com.google.gson.annotations.SerializedName

data class Consents(
    @SerializedName("personalization")
    val personalization: Personalization
) {
    data class Personalization(
        @SerializedName("consented")
        val consented: Boolean
    )
}