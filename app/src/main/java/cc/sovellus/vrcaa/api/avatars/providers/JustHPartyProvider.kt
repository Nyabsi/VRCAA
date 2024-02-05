package cc.sovellus.vrcaa.api.avatars.providers

import cc.sovellus.vrcaa.api.avatars.AvatarProviderContext
import cc.sovellus.vrcaa.api.avatars.models.JustHPartyAvatars
import com.google.gson.Gson
import java.net.URLEncoder

class JustHPartyProvider : AvatarProviderContext() {
    override val apiUrl = "https://avtr.just-h.party/vrcx_search.php"
    override val userAgent = "VRCAA/0.1"
    override val referer = "vrcaa.sovellus.cc"

    suspend fun search(type: String, query: String, n: Int): JustHPartyAvatars? {
        return when (val result = sendRequest("?${type}=${URLEncoder.encode(query)}&n=5000")) {
            is String -> {
                Gson().fromJson(result, JustHPartyAvatars::class.java)
            }
            else -> null
        }
    }
}