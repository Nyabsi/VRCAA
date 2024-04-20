package cc.sovellus.vrcaa.api.vrchat

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.activity.main.MainActivity
import cc.sovellus.vrcaa.api.base.BaseClient
import cc.sovellus.vrcaa.api.vrchat.models.FileMetadata
import cc.sovellus.vrcaa.api.vrchat.models.Friends
import cc.sovellus.vrcaa.api.vrchat.models.Group
import cc.sovellus.vrcaa.api.vrchat.models.GroupInstances
import cc.sovellus.vrcaa.api.vrchat.models.Groups
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.Notifications
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.api.vrchat.models.UserGroups
import cc.sovellus.vrcaa.api.vrchat.models.Users
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.api.vrchat.models.Worlds
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.helper.invalidCookie
import cc.sovellus.vrcaa.helper.twoFactorAuth
import cc.sovellus.vrcaa.service.PipelineService
import com.google.gson.Gson
import okhttp3.Headers
import java.net.URLEncoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class VRChatApi(
    private val context: Context
) : BaseClient() {

    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", 0)

    private val apiBase: String = "https://api.vrchat.cloud/api/1"
    private val userAgent: String = "VRCAA/0.1 nyabsi@sovellus.cc"
    private var cookies: String = ""

    init {
        cookies = preferences.cookies
    }

    enum class MfaType { NONE, EMAIL_OTP, OTP, TOTP }

    private fun invalidateSession() {

        preferences.cookies = ""
        preferences.invalidCookie = true

        val serviceIntent = Intent(context, PipelineService::class.java)
        context.stopService(serviceIntent)

        Toast.makeText(
            context,
            context.getString(R.string.api_session_has_expired_text),
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun handleRequest(result: Result, isAuthorization: Boolean = false): String? {
        return when (result) {
             is Result.Succeeded -> {
                if (BuildConfig.DEBUG)
                    Log.d("VRCAA", result.body)

                var cookies = ""

                if (result.response.headers("Set-Cookie").isNotEmpty()) {
                    for (cookie in result.response.headers("Set-Cookie")) { cookies += "$cookie " }
                    "${cookies}~${result.body}"
                }
                else
                    result.body
            }

            is Result.UnhandledResult -> {
                if (BuildConfig.DEBUG)
                    Log.d("VRCAA", "Unknown response type from server, ${result.response.code}")
                null
            }

            Result.InternalError -> {
                Toast.makeText(
                    context,
                    "Server responded with Internal Error. Please try again, or check https://status.vrchat.com/",
                    Toast.LENGTH_LONG
                ).show()
                null
            }

            Result.RateLimited -> {
                Toast.makeText(
                    context,
                    "You are being rate-limited, please wait a while before sending another request.",
                    Toast.LENGTH_LONG
                ).show()
                null
            }

            Result.Unauthorized -> {
                if (!isAuthorization && !preferences.invalidCookie)
                    invalidateSession()
                null
            }

            Result.UnknownMethod -> {
                throw RuntimeException("doRequest was called with unsupported method, supported methods are GET, POST, PUT and DELETE.")
            }

            else -> { null }
        }
    }



    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getToken(username: String, password: String): MfaType? {

        val token = Base64.encode((URLEncoder.encode(username).replace("+", "%20") + ":" + URLEncoder.encode(password).replace("+", "%20")).toByteArray())

        val headers = Headers.Builder()

        headers["Authorization"] = "Basic $token"
        headers["User-Agent"] = userAgent

        if (preferences.twoFactorAuth.isNotEmpty())
            headers["Cookie"] = preferences.twoFactorAuth

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user",
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result, true)

        response?.let {

            if (!response.contains("requiresTwoFactorAuth")) {
                val cookies = response.split('~')[0]
                preferences.cookies = cookies
                preferences.twoFactorAuth = cookies.substring(cookies.indexOf("twoFactorAuth="), cookies.indexOf(";", cookies.indexOf("twoFactorAuth=")))
                this.cookies = cookies
                preferences.invalidCookie = false
                return MfaType.NONE
            } else {
                // this is double encoded because I could not figure better way to handle headers.
                val cookies = response.split('~')[0]
                val body = response.split('~')[1]

                preferences.cookies = cookies
                this.cookies = cookies

                if (body.contains("emailOtp")) {
                    return MfaType.EMAIL_OTP
                } else {
                    return MfaType.TOTP
                }
            }
        }

        return null
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

        val response = handleRequest(result)
        return Gson().fromJson(response, cc.sovellus.vrcaa.api.vrchat.models.Auth::class.java)?.token
    }

    suspend fun verifyAccount(type: MfaType, code: String): Boolean {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        return when (type) {
            MfaType.EMAIL_OTP -> {

                val body = Gson().toJson(cc.sovellus.vrcaa.api.vrchat.models.Code(code))

                val result = doRequest(
                    method = "POST",
                    url = "$apiBase/auth/twofactorauth/emailotp/verify",
                    headers = headers.build(),
                    body = body
                )

                val response = handleRequest(result)

                response?.let {
                    val cookie = response.split('~')[0]
                    preferences.invalidCookie = false
                    preferences.cookies = "${preferences.cookies} $cookie"
                    preferences.twoFactorAuth = cookie
                    return true
                }
                return false
            }

            MfaType.TOTP -> {

                val body = Gson().toJson(cc.sovellus.vrcaa.api.vrchat.models.Code(code))

                val result = doRequest(
                    method = "POST",
                    url = "$apiBase/auth/twofactorauth/totp/verify",
                    headers = headers.build(),
                    body = body
                )

                val response = handleRequest(result)

                response?.let {
                    val cookie = response.split('~')[0]
                    preferences.invalidCookie = false
                    preferences.cookies = "${preferences.cookies} $cookie"
                    preferences.twoFactorAuth = cookie
                    return true
                }
                return false
            }

            else -> { false }
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

        return handleRequest(result) is String
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

        val response = handleRequest(result)
        return Gson().fromJson(response, User::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, Friends::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, LimitedUser::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, LimitedUser::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, Instance::class.java)
    }

    suspend fun inviteSelfToInstance(intent: String){

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        doRequest(
            method = "POST",
            url = "$apiBase/invite/myself/to/$intent",
            headers = headers.build(),
            body = null
        )
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

        val response = handleRequest(result)
        return Gson().fromJson(response, Worlds::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, Worlds::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, World::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, Users::class.java)
    }

    suspend fun getFavorites(type: String, n: Int = 50): cc.sovellus.vrcaa.api.vrchat.models.Favorites? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/favorites?type=$type&n=$n", // TODO: if ever needed, implement "tag"
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, cc.sovellus.vrcaa.api.vrchat.models.Favorites::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, Notifications::class.java)
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

        val response = handleRequest(result)
        return Gson().fromJson(response, User::class.java)
    }

    suspend fun getAvatar(avatarId: String): cc.sovellus.vrcaa.api.vrchat.models.Avatar? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/avatars/${avatarId}",
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, cc.sovellus.vrcaa.api.vrchat.models.Avatar::class.java)
    }

    suspend fun getGroups(query: String, n: Int): Groups? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/groups?query=$query&n=$n",
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, Groups::class.java)
    }

    suspend fun getGroups(userId: String): UserGroups? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/users/$userId/groups",
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, UserGroups::class.java)
    }

    suspend fun getGroup(groupId: String): Group? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/groups/$groupId?includeRoles=true&purpose=group",
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, Group::class.java)
    }

    suspend fun joinGroup(groupId: String): Boolean {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "POST",
            url = "$apiBase/groups/$groupId/join?confirmOverrideBlock=false",
            headers = headers.build(),
            body = null
        )

        return result is Result.Succeeded
    }

    suspend fun leaveGroup(groupId: String): Boolean {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "POST",
            url = "$apiBase/groups/$groupId/leave",
            headers = headers.build(),
            body = null
        )

        return result is Result.Succeeded
    }

    suspend fun withdrawGroupJoinRequest(groupId: String): Boolean {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "DELETE",
            url = "$apiBase/groups/$groupId/requests",
            headers = headers.build(),
            body = null
        )

        return result is Result.Succeeded
    }

    suspend fun getGroupInstances(groupId: String): GroupInstances? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/groups/$groupId/instances",
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, GroupInstances::class.java)
    }

    suspend fun getFileMetadata(fileId: String): FileMetadata? {

        val headers = Headers.Builder()

        headers["Cookie"] = cookies
        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/file/$fileId",
            headers = headers.build(),
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, FileMetadata::class.java)
    }
}