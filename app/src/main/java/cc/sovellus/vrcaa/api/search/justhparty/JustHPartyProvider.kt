package cc.sovellus.vrcaa.api.search.justhparty

import cc.sovellus.vrcaa.api.BaseClient
import cc.sovellus.vrcaa.api.search.SearchAvatar
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Headers
import java.net.URLEncoder

class JustHPartyProvider : BaseClient() {

    private suspend fun sendRequest(params: String): String?
    {
        val headers = Headers.Builder()
        headers["User-Agent"] = "VRCAA/0.1"
        headers["Referer"] = "vrcaa.sovellus.cc"

        val result = doRequest(
            method = "GET",
            url = "https://avtr.just-h.party/vrcx_search.php$params",
            headers = headers,
            body = null
        )

        return when (result) {
            is Result.Succeeded -> {
                return result.body
            }
            else -> {
                null
            }
        }
    }

    suspend fun search(query: String): ArrayList<SearchAvatar>
    {
        return when (val result = sendRequest("?search=${URLEncoder.encode(query)}&n=5000")) {
            is String -> {
                Gson().fromJson(result, Avatars::class.java) ?: arrayListOf()
            }
            else -> arrayListOf()
        }
    }

    class Avatars : ArrayList<SearchAvatar>()
}