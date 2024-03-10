package cc.sovellus.vrcaa.api

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.activity.main.MainActivity
import cc.sovellus.vrcaa.api.models.Auth
import cc.sovellus.vrcaa.api.models.Avatar
import cc.sovellus.vrcaa.api.models.Avatars
import cc.sovellus.vrcaa.api.models.Favorites
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.Instance
import cc.sovellus.vrcaa.api.models.Worlds
import cc.sovellus.vrcaa.api.models.Notifications
import cc.sovellus.vrcaa.api.models.User
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.api.models.Users
import cc.sovellus.vrcaa.api.models.World
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.helper.isExpiredSession
import cc.sovellus.vrcaa.helper.twoFactorAuth
import cc.sovellus.vrcaa.service.PipelineService
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
    context: Context
) : ContextWrapper(context) {

    private val client: OkHttpClient = OkHttpClient()
    private val preferences: SharedPreferences = getSharedPreferences("vrcaa_prefs", 0)

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

    private suspend fun doRequest(
        method: String,
        url: String,
        headers: Headers,
        body: String?
    ): Any? = when (method) {
        "GET" -> {
            val request =
                Request.Builder()
                    .headers(headers = headers)
                    .url(url)
                    .get()
                    .build()

            val response = client.newCall(request).await()

            when (response.code) {
                200 -> {
                    response
                }

                429 -> {
                    Toast.makeText(
                        this,
                        "You are being rate-limited, calm down.",
                        Toast.LENGTH_LONG
                    ).show()
                    null
                }

                401 -> {
                    if (!url.contains("auth/user") && !preferences.isExpiredSession) {
                        refreshToken()
                    } else {
                        null
                    }
                }

                500 -> {
                    /* TODO: handle 500 error */
                }

                else -> {
                    response.body?.let {
                        Log.d(
                            "VRCAA",
                            "Got unhandled response from server (${response.code}): ${it.string()}"
                        )
                    }
                    null
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

            when (response.code) {
                200 -> {
                    response
                }

                429 -> {
                    Toast.makeText(
                        this,
                        "You are being rate-limited, calm down.",
                        Toast.LENGTH_LONG
                    ).show()
                    null
                }

                401 -> {
                    if (!url.contains("auth/user") && !preferences.isExpiredSession) {
                        refreshToken()
                    } else {
                        null
                    }
                }

                500 -> {
                    /* TODO: handle 500 error */
                }

                else -> {
                    response.body?.let {
                        Log.d(
                            "VRCAA",
                            "Got unhandled response from server (${response.code}): ${it.string()}"
                        )
                    }
                    null
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

            when (response.code) {
                200 -> {
                    response
                }

                429 -> {
                    Toast.makeText(
                        this,
                        "You are being rate-limited, calm down.",
                        Toast.LENGTH_LONG
                    ).show()
                    null
                }

                401 -> {
                    if (!url.contains("auth/user") && !preferences.isExpiredSession) {
                        refreshToken()
                    } else {
                        null
                    }
                }

                500 -> {
                    /* TODO: handle 500 error */
                }

                else -> {
                    response.body?.let {
                        Log.d(
                            "VRCAA",
                            "Got unhandled response from server (${response.code}): ${it.string()}"
                        )
                    }
                    null
                }
            }
        }

        else -> {
            null
        }
    }

    private fun refreshToken() {

        // just tell the user to re-login, I give up.
        Toast.makeText(
            this,
            getString(R.string.api_session_has_expired_text),
            Toast.LENGTH_LONG
        ).show()

        preferences.cookies = ""
        preferences.isExpiredSession = true

        val serviceIntent = Intent(this, PipelineService::class.java)
        stopService(serviceIntent)

        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @Suppress("DEPRECATION")
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getToken(username: String, password: String): Pair<TwoFactorType, String>? {

        val token =
            Base64.encode((URLEncoder.encode(username) + ":" + URLEncoder.encode(password)).toByteArray())

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
                if (result.body?.string()?.contains("emailOtp") == true) {
                    Pair(TwoFactorType.EMAIL_OTP, result.headers["Set-Cookie"].toString())
                } else {
                    Pair(TwoFactorType.TOTP, result.headers["Set-Cookie"].toString())
                }
            }

            else -> {
                null
            }
        }
    }

    suspend fun getAuth(): String? {
        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Auth::class.java).token
            }

            else -> {
                null
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

            TwoFactorType.TOTP -> {

                val result = doRequest(
                    method = "POST",
                    url = "$apiBase/auth/twofactorauth/totp/verify",
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

    suspend fun getFriend(userId: String): LimitedUser? {

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

    suspend fun getWorlds(
        query: String = "",
        featured: Boolean = false,
        n: Int = 50,
        sort: String = "relevance"
    ): Worlds? {

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

    suspend fun getWorld(id: String): World? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds/$id",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), World::class.java)
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

    suspend fun getFavorites(type: String, n: Int = 50): Favorites? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/favorites?type=$type&n=$n", // TODO: if ever needed, implement "tag"
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Favorites::class.java)
            }

            else -> {
                null
            }
        }
    }

    suspend fun getNotifications(): Notifications? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user/notifications",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Notifications::class.java)
            }

            else -> {
                null
            }
        }
    }

    suspend fun selectAvatar(avatarId: String): User? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "PUT",
            url = "$apiBase/avatars/${avatarId}/select",
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

    suspend fun getAvatar(avatarId: String): Avatar? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/avatars/${avatarId}",
            headers = headers.build(),
            body = null
        )

        return when (result) {
            is Response -> {
                Gson().fromJson(result.body?.string(), Avatar::class.java)
            }

            else -> {
                null
            }
        }
    }
}