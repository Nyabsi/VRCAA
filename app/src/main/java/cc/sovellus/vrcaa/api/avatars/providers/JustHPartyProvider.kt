package cc.sovellus.vrcaa.api.avatars.providers

import android.util.Log
import cc.sovellus.vrcaa.api.avatars.AvatarProviderContext
import java.net.URLEncoder

class JustHPartyProvider : AvatarProviderContext() {
    override val apiUrl = "https://avtr.just-h.party/vrcx_search.php"
    override val userAgent = "VRCAA/0.1"
    override val referer = "vrcaa.sovellus.cc"

    suspend fun search(type: String, query: String, n: Int) {
        val result = sendRequest("?${type}=${URLEncoder.encode(query)}&n=5000")
    }
}