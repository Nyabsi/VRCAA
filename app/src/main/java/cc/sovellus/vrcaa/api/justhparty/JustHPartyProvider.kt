package cc.sovellus.vrcaa.api.justhparty

import android.util.Log
import cc.sovellus.vrcaa.api.justhparty.models.JustHPartyAvatars
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await
import java.net.URLEncoder

class JustHPartyProvider {

    private val client: OkHttpClient by lazy { OkHttpClient() }

    private val apiUrl = "https://avtr.just-h.party/vrcx_search.php"
    private val userAgent = "VRCAA/0.1"
    private val referer = "vrcaa.sovellus.cc"

    private suspend fun sendRequest(urlParams: String): String? {
        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent
        headers["Referer"] = referer

        val request =
            Request.Builder()
                .headers(headers = headers.build())
                .url("${apiUrl}${urlParams}")
                .get()
                .build()

        val response = client.newCall(request).await()

        return when (response.code) {
            200 -> response.body?.string()
            else -> {
                Log.d("VRCAA", "Failed to fetch avatar data from provider, code: ${response.code} and body is ${response.body?.string()}")
                null
            }
        }
    }

    suspend fun search(type: String, query: String, n: Int): JustHPartyAvatars? {
        return when (val result = sendRequest("?${type}=${URLEncoder.encode(query)}&n=5000")) {
            is String -> {
                Gson().fromJson(result, JustHPartyAvatars::class.java)
            }
            else -> null
        }
    }
}