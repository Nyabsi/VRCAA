package cc.sovellus.vrcaa.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import cc.sovellus.vrcaa.api.models.Avatars
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.Instance
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.api.models.User
import cc.sovellus.vrcaa.api.models.Users
import cc.sovellus.vrcaa.api.models.Worlds
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.helper.twoFactorAuth
import cc.sovellus.vrcaa.helper.userCredentials
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.gildor.coroutines.okhttp.await
import java.net.URLEncoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ApiContext(
    private var context: Context
) {

    private val client: OkHttpClient = OkHttpClient()
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", 0)

    private val apiBase: String = "https://api.vrchat.cloud/api/1"
    private val userAgent: String = "VRCAA/0.1 nyabsi@sovellus.cc"
    private var cookies: String = ""

    init {
        cookies = "${preferences.cookies} ${preferences.twoFactorAuth}"
    }

    enum class TwoFactorType {
        EMAIL_OTP,
        OTP,
        TOTP
    }

    suspend fun doRequest(
        method: String,
        url: String,
        headers: Headers,
        body: String?
    ): Response? {
        return when (method)
        {
            "GET" -> {
                val request =
                    Request.Builder()
                        .headers(headers = headers)
                        .url(url)
                        .get()
                        .build()

                val response = client.newCall(request).await()

                if (response.code == 200) {
                    response
                } else {
                    return when (response.code) {
                        429 -> {
                            Toast.makeText(
                                context,
                                "You are being rate-limited, calm down.",
                                Toast.LENGTH_LONG
                            ).show()
                            null
                        }
                        401 -> {
                            refreshToken()
                            doRequest(method, url, headers, body)
                        }
                        else -> {
                            response.body?.let {
                                Log.d("VRCAA", "Got unhandled response from server (${response.code}): ${it.string()}")
                            }
                            null
                        }
                    }
                }
            }

            "POST" -> {

                val type: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody: RequestBody = body!!.toRequestBody(type)

                val request =
                    Request.Builder()
                        .headers(headers = headers)
                        .url(url)
                        .post(requestBody)
                        .build()

                val response = client.newCall(request).await()

                if (response.code == 200) {
                    response
                } else {
                    return when (response.code) {
                        429 -> {
                            Toast.makeText(
                                context,
                                "You are being rate-limited, calm down.",
                                Toast.LENGTH_LONG
                            ).show()
                            null
                        }
                        401 -> {
                            refreshToken()
                            doRequest(method, url, headers, body)
                        }
                        else -> {
                            response.body?.let {
                                Log.d("VRCAA", "Got unhandled response from server (${response.code}): ${it.string()}")
                            }
                            null
                        }
                    }
                }
            }

            "PUT" -> {

                val request =
                    Request.Builder()
                        .headers(headers = headers)
                        .url(url)
                        .put(EMPTY_REQUEST)
                        .build()

                val response = client.newCall(request).await()

                if (response.code == 200) {
                    response
                } else {
                    return when (response.code) {
                        429 -> {
                            Toast.makeText(
                                context,
                                "You are being rate-limited, calm down.",
                                Toast.LENGTH_LONG
                            ).show()
                            null
                        }
                        401 -> {
                            refreshToken()
                            doRequest(method, url, headers, body)
                        }
                        else -> {
                            response.body?.let {
                                Log.d("VRCAA", "Got unhandled response from server (${response.code}): ${it.string()}")
                            }
                            null
                        }
                    }
                }
            }

            else -> {
                Log.d("VRCAA", "Unsupported request type, how did you end up here?")
                null
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun refreshToken() {

        val headers = Headers.Builder()
        val token = Base64.encode((URLEncoder.encode(preferences.userCredentials.first) + ":" + URLEncoder.encode(preferences.userCredentials.second)).toByteArray())

        headers["Authorization"] = "Basic $token"
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user",
            headers = headers.build(),
            body = null
        )

        when (result) {
            is Response -> {
                preferences.cookies = result.headers["Set-Cookie"].toString()
            }
            else -> {
                Toast.makeText(
                    context,
                    "Something went horribly wrong when refreshing access token, contact the developer.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @Suppress("DEPRECATION")
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getToken(username: String, password: String): String {

        val token = Base64.encode((URLEncoder.encode(username) + ":" + URLEncoder.encode(password)).toByteArray())

        val headers = Headers.Builder()

        headers["Authorization"] = "Basic $token"
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                result.headers["Set-Cookie"].toString()
            }
            else -> {
                ""
            }
        }
    }

    suspend fun verifyAccount(token: String, type: TwoFactorType, code: String): String {

        val headers = Headers.Builder()

        headers["Cookie"] = token
        headers["User-Agent"] = userAgent

        return when (type) {
            TwoFactorType.EMAIL_OTP -> {

                val result = doRequest(
                    method = "POST",
                    url = "$apiBase/auth/twofactorauth/emailotp/verify",
                    headers = headers.build(),
                    body = "{\"code\":\"$code\"}"
                )

                when (result) {
                    is Response -> {
                        result.headers["twoFactorAuth"].toString()
                    }
                    else -> {
                        ""
                    }
                }
            }
            TwoFactorType.OTP -> {
                "not_implemented"
            }
            TwoFactorType.TOTP -> {
                "not_implemented"
            }
        }
    }

    suspend fun logout(): Boolean {
        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "PUT",
            url = "$apiBase/logout",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                true
            }
            else -> {
                false
            }
        }
    }

    suspend fun getSelf(): User? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), User::class.java)
            }
            else -> {
                null
            }
        }
    }

    suspend fun getFriends(offline: Boolean = false): Friends? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user/friends?offline=$offline",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Friends::class.java)
            }
            else -> {
                null
            }
        }
    }

    @Suppress("unused")
    suspend fun getUser(userId: String): LimitedUser? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/users/$userId",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), LimitedUser::class.java)
            }
            else -> {
                null
            }
        }
    }

    // Intent is compromised of <worldId>:<InstanceId>:<Nonce>
    // NOTE: `<Nonce>` is only used for private instances.
    suspend fun getInstance(intent: String): Instance? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/instances/$intent",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
               Gson().fromJson(result.body?.string(), Instance::class.java)
            }
            else -> {
               null
            }
        }
    }

    suspend fun getRecentWorlds(): Worlds? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds/recent?featured=false",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Worlds::class.java)
            }
            else -> {
                null
            }
        }
    }

    suspend fun getWorlds(query: String = "", featured: Boolean = false, n: Int = 50, sort: String = "relevance"): Worlds? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds?featured=$featured&n=$n&sort=$sort&search=$query",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Worlds::class.java)
            }
            else -> {
                null
            }
        }
    }

    suspend fun getAvatars(featured: Boolean = true, n: Int = 50): Avatars? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/avatars?featured=$featured&n=$n",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Avatars::class.java)
            }
            else -> {
                null
            }
        }
    }

    suspend fun getUsers(username: String, n: Int = 50): Users? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/users?search=$username&n=$n",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Users::class.java)
            }
            else -> {
                null
            }
        }
    }
}