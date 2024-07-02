package cc.sovellus.vrcaa.api.discord

import android.util.Log
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.api.BaseClient
import cc.sovellus.vrcaa.api.discord.models.WebHookResponse
import com.google.gson.Gson
import okhttp3.Headers

class DiscordMediaProxy(
    private val webHookUrl: String
)  : BaseClient() {

    private val apiBase: String = "https://discord.com/api/v9"
    private val userAgent: String = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:123.0) Gecko/20100101 Firefox/123.0"

    private fun handleRequest(result: Result): String? {
        return when (result) {
            is Result.Succeeded -> {
                if (BuildConfig.DEBUG)
                    Log.d("VRCAA", result.body)
                result.body
            }

            is Result.UnhandledResult -> {
                if (BuildConfig.DEBUG)
                    Log.d("VRCAA", "Unknown response type from server, ${result.response.code}")
                null
            }

            Result.UnknownMethod -> {
                throw RuntimeException("doRequest was called with unsupported method, supported methods are GET, POST and PUT.")
            }

            else -> { null }
        }
    }

    suspend fun convertImageUrl(url: String?): String? {

        if (url == null)
            return ""

        val headers = Headers.Builder()
        headers["User-Agent"] = userAgent

        val body = "{\"content\":null,\"embeds\":[{\"color\":null,\"image\":{\"url\":\"$url\"}}],\"attachments\":[]}"

        val result = doRequest(
            method = "POST",
            url = "$webHookUrl?wait=true",
            headers = headers,
            body = body
        )

        val response = handleRequest(result)

        if (result == Result.InvalidRequest)
            return null

        val webhookResponse =  Gson().fromJson(response, WebHookResponse::class.java)
        return "mp:external/${webhookResponse.embeds[0].image.proxyUrl.split('/')[4]}/${url.replace(":/", "")}"
    }
}