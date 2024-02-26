package cc.sovellus.vrcaa.api.avatars

import android.util.Log
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

open class AvatarProviderContext {

    private val client: OkHttpClient by lazy { OkHttpClient() }

    // These should be populated in actual "providers"
    open val apiUrl = ""
    open val userAgent = ""
    open val referer = ""

    suspend fun sendRequest(urlParams: String): String? {
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
                Log.d(
                    "VRCAA",
                    "Failed to fetch avatar data from provider, code: ${response.code} and body is ${response.body?.string()}"
                )
                null
            }
        }
    }
}