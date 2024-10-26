package cc.sovellus.vrcaa.api.discord

import android.util.Log
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.api.BaseClient
import cc.sovellus.vrcaa.api.discord.models.DiscordLogin
import cc.sovellus.vrcaa.api.discord.models.DiscordTicket
import com.google.gson.Gson
import okhttp3.Headers

class DiscordApi : BaseClient() {

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

    suspend fun login(username: String, password: String): Any {
        
        val headers = Headers.Builder()
        headers["User-Agent"] = userAgent

        val body = "{\"login\":\"$username\",\"password\":\"$password\",\"undelete\":false,\"login_source\":null,\"gift_code_sku_id\":null}"

        val result = doRequest(
            method = "POST",
            url = "$apiBase/auth/login",
            headers = headers,
            body = body
        )

        val response = handleRequest(result)

        return if (result == Result.InvalidRequest) {
            false
        } else {
            if (response?.contains("ticket") == true) {
                val ticket = Gson().fromJson(response, DiscordTicket::class.java)
                ticket
            } else {
                val login = Gson().fromJson(response, DiscordLogin::class.java)
                login
            }
        }
    }

    suspend fun mfa(ticket: String, code: String): DiscordLogin? {
        val headers = Headers.Builder()
        headers["User-Agent"] = userAgent

        val body = "{\"code\":\"$code\",\"ticket\":\"$ticket\",\"login_source\":null,\"gift_code_sku_id\":null}"

        val result = doRequest(
            method = "POST",
            url = "$apiBase/auth/mfa/totp",
            headers = headers,
            body = body
        )

        val response = handleRequest(result)

        return if (result == Result.InvalidRequest)
            null
        else
            Gson().fromJson(response, DiscordLogin::class.java)
    }
}