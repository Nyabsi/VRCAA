package cc.sovellus.vrcaa.api.vrchat

import android.util.Log
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.api.BaseClient
import cc.sovellus.vrcaa.api.vrchat.models.Favorite
import cc.sovellus.vrcaa.api.vrchat.models.Favorites
import cc.sovellus.vrcaa.api.vrchat.models.FileMetadata
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.Friends
import cc.sovellus.vrcaa.api.vrchat.models.Group
import cc.sovellus.vrcaa.api.vrchat.models.GroupInstances
import cc.sovellus.vrcaa.api.vrchat.models.Groups
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.Notifications
import cc.sovellus.vrcaa.api.vrchat.models.SteamCount
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.api.vrchat.models.UserGroups
import cc.sovellus.vrcaa.api.vrchat.models.Users
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.api.vrchat.models.Worlds
import cc.sovellus.vrcaa.manager.ApiManager.api
import com.google.gson.Gson
import okhttp3.Headers
import java.net.URLEncoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class VRChatApi : BaseClient() {

    private val apiBase: String = "https://api.vrchat.cloud/api/1"
    private val userAgent: String = "VRCAA/0.1 nyabsi@sovellus.cc"

    private var listener: SessionListener? = null

    interface SessionListener {
        fun onSessionInvalidate()
        fun noInternet()
    }

    fun setSessionListener(listener: SessionListener) {
        this.listener = listener
    }

    enum class MfaType {
        NONE,
        EMAIL_OTP,
        TOTP
    }

    data class AccountInfo(val mfaType: MfaType, val token: String, val twoAuth: String = "")

    private fun handleRequest(result: Result): String? {
        return when (result) {
             is Result.Succeeded -> {
                if (BuildConfig.DEBUG)
                    Log.d("VRCAA", result.body)

                var cookies = ""

                 if (result.body == "[]")
                     return null

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

            is Result.ClientExceptionResult -> {
                listener?.noInternet()
                null
            }

            Result.RateLimited -> {
                null
            }

            Result.Unauthorized -> {
                listener?.onSessionInvalidate()
                null
            }

            Result.UnknownMethod -> {
                throw RuntimeException("doRequest was called with unsupported method, supported methods are GET, POST, PUT and DELETE.")
            }

            Result.NotModified -> {
                null
            }

            else -> { null }
        }
    }

    fun setToken(token: String) {
        setAuthorization(AuthorizationType.Cookie, token)
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getToken(username: String, password: String, twoFactor: String): AccountInfo? {

        val token = Base64.encode((URLEncoder.encode(username).replace("+", "%20") + ":" + URLEncoder.encode(password).replace("+", "%20")).toByteArray())

        val headers = Headers.Builder()

        headers["Authorization"] = "Basic $token"
        headers["User-Agent"] = userAgent

        if (twoFactor.isNotEmpty())
            api.setToken(twoFactor)

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            val cookies = response.split('~')[0]
            val body = response.split('~')[1]

            api.setToken(cookies)

            if (!body.contains("requiresTwoFactorAuth"))
                return AccountInfo(MfaType.NONE, cookies)

            if (body.contains("emailOtp")) {
                return AccountInfo(MfaType.EMAIL_OTP, cookies)
            } else {
                return AccountInfo(MfaType.TOTP, cookies)
            }
        }

        return null
    }

    suspend fun getAuth(): String? {
        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, cc.sovellus.vrcaa.api.vrchat.models.Auth::class.java)?.token
    }

    suspend fun verifyAccount(type: MfaType, code: String): String? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        return when (type) {
            MfaType.EMAIL_OTP -> {

                val body = Gson().toJson(cc.sovellus.vrcaa.api.vrchat.models.Code(code))

                val result = doRequest(
                    method = "POST",
                    url = "$apiBase/auth/twofactorauth/emailotp/verify",
                    headers = headers,
                    body = body
                )

                val response = handleRequest(result)

                response?.let {
                    return response.split('~')[0]
                }
                return null
            }

            MfaType.TOTP -> {

                val body = Gson().toJson(cc.sovellus.vrcaa.api.vrchat.models.Code(code))

                val result = doRequest(
                    method = "POST",
                    url = "$apiBase/auth/twofactorauth/totp/verify",
                    headers = headers,
                    body = body
                )

                val response = handleRequest(result)

                response?.let {
                    return response.split('~')[0]
                }
                return null
            }

            else -> { null }
        }
    }

    suspend fun logout(): Boolean {
        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "PUT",
            url = "$apiBase/logout",
            headers = headers,
            body = null
        )

        return handleRequest(result) is String
    }

    suspend fun getSelf(): User? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, User::class.java)
    }

    suspend fun getFriends(
        offline: Boolean,
        n: Int = 50,
        offset: Int = 0,
        friends: ArrayList<Friend> = arrayListOf()
    ): ArrayList<Friend> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user/friends?offline=$offline&n=$n&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        val temp: ArrayList<Friend> = friends
        val json = Gson().fromJson(response, Friends::class.java)
        json?.forEach { friend ->
            temp.add(friend)
        }

        return if (json == null) {
            friends
        } else {
            getFriends(offline, n, offset + n, temp)
        }
    }

    suspend fun getUser(userId: String): LimitedUser? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/users/$userId",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, LimitedUser::class.java)
    }

    // Intent is compromised of <worldId>:<InstanceId>:<Nonce>
    // NOTE: `<Nonce>` is only used for private instances.
    suspend fun getInstance(intent: String): Instance {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/instances/$intent",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, Instance::class.java)
    }

    suspend fun inviteSelfToInstance(intent: String){

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        doRequest(
            method = "POST",
            url = "$apiBase/invite/myself/to/$intent",
            headers = headers,
            body = null
        )
    }

    suspend fun getRecentWorlds(): Worlds? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds/recent?featured=false",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, Worlds::class.java)
    }

    suspend fun getWorlds(
        query: String = "",
        limit: Int,
        featured: Boolean = false,
        sort: String = "relevance",
        n: Int = 50,
        offset: Int = 0,
        worlds: ArrayList<World> = arrayListOf()
    ): ArrayList<World> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds?featured=$featured&n=$n&sort=$sort&search=$query&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        val temp: ArrayList<World> = worlds
        val json = Gson().fromJson(response, Worlds::class.java)
        json?.forEach { world ->
            temp.add(world)
        }

        return if (json == null || limit == (offset + n)) {
            worlds
        } else {
            getWorlds(query, limit, featured, sort, n, offset + n, worlds)
        }
    }

    suspend fun getWorld(id: String): World {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds/$id",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, World::class.java)
    }

    suspend fun getUsers(
        username: String,
        limit: Int,
        n: Int = 50,
        offset: Int = 0,
        users: ArrayList<LimitedUser> = arrayListOf()
    ): ArrayList<LimitedUser> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/users?search=$username&n=$n&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        val temp: ArrayList<LimitedUser> = users
        val json = Gson().fromJson(response, Users::class.java)
        json?.forEach { user ->
            temp.add(user)
        }

        return if (json == null  || limit == (offset + n)) {
            users
        } else {
            getUsers(username, limit, n, offset + n, temp)
        }
    }

    suspend fun getFavorites(
        type: String,
        n: Int = 50,
        offset: Int = 0,
        favorites: ArrayList<Favorite> = arrayListOf()
    ): ArrayList<Favorite> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/favorites?type=$type&n=$n&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        val temp: ArrayList<Favorite> = favorites
        val json = Gson().fromJson(response, Favorites::class.java)
        json?.forEach { favorite ->
            temp.add(favorite)
        }

        return if (json == null) {
            favorites
        } else {
            getFavorites(type, n, offset + n, temp)
        }
    }

    suspend fun getNotifications(): Notifications? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user/notifications",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, Notifications::class.java)
    }

    suspend fun selectAvatar(avatarId: String): User? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "PUT",
            url = "$apiBase/avatars/${avatarId}/select",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, User::class.java)
    }

    suspend fun getAvatar(avatarId: String): cc.sovellus.vrcaa.api.vrchat.models.Avatar? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/avatars/${avatarId}",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, cc.sovellus.vrcaa.api.vrchat.models.Avatar::class.java)
    }

    suspend fun getGroups(
        query: String,
        limit: Int,
        n: Int = 50,
        offset: Int = 0,
        groups: ArrayList<Group> = arrayListOf()
    ): ArrayList<Group> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/groups?query=$query&n=$n&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        val temp: ArrayList<Group> = groups
        val json = Gson().fromJson(response, Groups::class.java)
        json?.forEach { group ->
            temp.add(group)
        }

        return if (json == null  || limit == (offset + n)) {
            groups
        } else {
            getGroups(query, limit, n, offset + n, groups)
        }
    }

    suspend fun getUserGroups(
        userId: String,
        n: Int = 50,
        offset: Int = 0,
        groups: ArrayList<UserGroups.Group> = arrayListOf()
    ): ArrayList<UserGroups.Group> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/users/$userId/groups&n=$n&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        val temp: ArrayList<UserGroups.Group> = groups
        val json = Gson().fromJson(response, UserGroups::class.java)
        json?.forEach { group ->
            temp.add(group)
        }

        return if (json == null) {
            groups
        } else {
            getUserGroups(userId, n, offset + n, groups)
        }
    }

    suspend fun getGroup(groupId: String): Group? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/groups/$groupId?includeRoles=true&purpose=group",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, Group::class.java)
    }

    suspend fun joinGroup(groupId: String): Boolean {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "POST",
            url = "$apiBase/groups/$groupId/join?confirmOverrideBlock=false",
            headers = headers,
            body = null
        )

        return result is Result.Succeeded
    }

    suspend fun leaveGroup(groupId: String): Boolean {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "POST",
            url = "$apiBase/groups/$groupId/leave",
            headers = headers,
            body = null
        )

        return result is Result.Succeeded
    }

    suspend fun withdrawGroupJoinRequest(groupId: String): Boolean {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "DELETE",
            url = "$apiBase/groups/$groupId/requests",
            headers = headers,
            body = null
        )

        return result is Result.Succeeded
    }

    suspend fun getGroupInstances(groupId: String): GroupInstances? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/groups/$groupId/instances",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, GroupInstances::class.java)
    }

    suspend fun getFileMetadata(fileId: String): FileMetadata? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/file/$fileId",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, FileMetadata::class.java)
    }

    suspend fun updateProfile(id: String, status: String, description: String, bio: String, bioLinks: List<String>): User? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val body = "{\"status\":\"$status\",\"statusDescription\":\"$description\",\"bio\":\"${bio.replace("\n", "\\n")}\",\"bioLinks\":${Gson().toJson(bioLinks)}}"

        val result = doRequest(
            method = "PUT",
            url = "$apiBase/users/$id",
            headers = headers,
            body = body
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, User::class.java)
    }

    suspend fun getVisits(): Int {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/visits",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)
        return response?.toInt() ?: -1
    }

    suspend fun getSteamConcurrent(): Int {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?format=json&appid=438100",
            headers = headers,
            body = null,
            ignoreAuthorization = true
        )

        val response = handleRequest(result)
        return Gson().fromJson(response, SteamCount::class.java).response.playerCount
    }
}