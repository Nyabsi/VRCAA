package cc.sovellus.vrcaa.api.vrchat

import cc.sovellus.vrcaa.api.BaseClient
import cc.sovellus.vrcaa.api.vrchat.models.Auth
import cc.sovellus.vrcaa.api.vrchat.models.Avatar
import cc.sovellus.vrcaa.api.vrchat.models.Avatars
import cc.sovellus.vrcaa.api.vrchat.models.Favorite
import cc.sovellus.vrcaa.api.vrchat.models.FavoriteAdd
import cc.sovellus.vrcaa.api.vrchat.models.FavoriteAvatar
import cc.sovellus.vrcaa.api.vrchat.models.FavoriteAvatars
import cc.sovellus.vrcaa.api.vrchat.models.FavoriteGroups
import cc.sovellus.vrcaa.api.vrchat.models.FavoriteLimits
import cc.sovellus.vrcaa.api.vrchat.models.FavoriteWorld
import cc.sovellus.vrcaa.api.vrchat.models.FavoriteWorlds
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
import cc.sovellus.vrcaa.helper.MathHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import com.google.gson.Gson
import okhttp3.Headers
import java.net.URLEncoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class VRChatApi : BaseClient() {

    private val apiBase: String = "https://api.vrchat.cloud/api/1"
    private val userAgent: String = "VRCAA/2.1.0 nyabsi@sovellus.cc"

    private var listener: SessionListener? = null

    interface SessionListener {
        fun onSessionInvalidate()
        fun noInternet()
    }

    fun setSessionListener(listener: SessionListener) {
        this.listener = listener
    }

    enum class MfaType {
        UNKNOWN,
        NONE,
        EMAIL_OTP,
        APP_OTP
    }

    data class AccountInfo(val mfaType: MfaType, val token: String = "", val twoAuth: String = "")

    private fun handleRequest(result: Result): List<String>? {
        return when (result) {
            is Result.Succeeded -> {
                var cookies = ""

                if (result.body == "[]")
                    return null

                if (result.response.headers("Set-Cookie").isNotEmpty()) {
                    for (cookie in result.response.headers("Set-Cookie")) {
                        cookies += "$cookie "
                    }
                    listOf(result.body, cookies)
                } else
                    listOf(result.body)
            }

            is Result.UnhandledResult -> {
                null
            }

            is Result.NoInternet -> {
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

            else -> {
                null
            }
        }
    }

    fun setToken(token: String) {
        setAuthorization(AuthorizationType.Cookie, token)
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getToken(username: String, password: String, twoFactor: String): AccountInfo? {

        val token = Base64.encode(
            (URLEncoder.encode(username).replace("+", "%20") + ":" + URLEncoder.encode(password)
                .replace("+", "%20")).toByteArray()
        )

        val headers = Headers.Builder()

        headers["Authorization"] = "Basic $token"
        headers["User-Agent"] = userAgent

        if (twoFactor.isNotEmpty())
            api.setToken(twoFactor)

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user",
            headers = headers,
            body = null,
            bypassIgnore = true
        )

        val response = handleRequest(result)

        response?.let {

            // No headers in response
            if (response.size == 1)
                return AccountInfo(MfaType.NONE, "")

            val body = response[0]
            val cookies = response[1]

            api.setToken(cookies)

            if (body.contains("emailOtp")) {
                return AccountInfo(MfaType.EMAIL_OTP, cookies)
            }

            if (body.contains("totp")) {
                return AccountInfo(MfaType.APP_OTP, cookies)
            }

            return AccountInfo(MfaType.NONE, cookies)
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

        response?.let {
            return Gson().fromJson(response[0], Auth::class.java)?.token
        }

        return null
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
                    return response[1]
                }
                return null
            }

            MfaType.APP_OTP -> {

                val body = Gson().toJson(cc.sovellus.vrcaa.api.vrchat.models.Code(code))

                val result = doRequest(
                    method = "POST",
                    url = "$apiBase/auth/twofactorauth/totp/verify",
                    headers = headers,
                    body = body
                )

                val response = handleRequest(result)

                response?.let {
                    return response[1]
                }
                return null
            }

            else -> {
                null
            }
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

        val response = handleRequest(result)

        response?.let {
            return true
        }

        return false
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

        response?.let {
            return Gson().fromJson(response[0], User::class.java)
        }
        return null
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

        response?.let {
            val temp: ArrayList<Friend> = friends
            val json = Gson().fromJson(response[0], Friends::class.java)
            json?.forEach { friend ->
                temp.add(friend)
            }

            getFriends(offline, n, offset + n, temp)
        }

        return friends
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

        response?.let {
            return Gson().fromJson(response[0], LimitedUser::class.java)
        }

        return null
    }

    // Intent is compromised of <worldId>:<InstanceId>:<Nonce>
    // NOTE: `<Nonce>` is only used for private instances.
    suspend fun getInstance(intent: String): Instance? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/instances/$intent",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            return Gson().fromJson(response[0], Instance::class.java)
        }

        return null
    }

    suspend fun inviteSelfToInstance(intent: String) {

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

        response?.let {
            return Gson().fromJson(response[0], Worlds::class.java)
        }

        return null
    }

    suspend fun getWorlds(
        query: String = "",
        limit: Int,
        byProduct: Int,
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

        response?.let {
            val temp: ArrayList<World> = worlds
            val json = Gson().fromJson(response[0], Worlds::class.java)
            json?.forEach { world ->
                temp.add(world)
            }

            getWorlds(
                query,
                limit,
                byProduct,
                featured,
                sort,
                if (MathHelper.isWithinByProduct(offset + n, byProduct, limit)) {
                    byProduct
                } else {
                    n
                },
                offset + n,
                worlds
            )
        }

        return worlds
    }

    suspend fun getWorldsByUserId(
        userId: String,
        private: Boolean = false,
        n: Int = 50,
        offset: Int = 0,
        worlds: ArrayList<World> = arrayListOf()
    ): ArrayList<World> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val releaseStatus = if (private) {
            "all"
        } else {
            "public"
        }

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds?releaseStatus=$releaseStatus&sort=updated&order=descending&userId=$userId&n=$n&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            val temp: ArrayList<World> = worlds
            val json = Gson().fromJson(response[0], Worlds::class.java)
            json?.forEach { world ->
                temp.add(world)
            }

            getWorldsByUserId(userId, private, n, offset + n, worlds)
        }

        return worlds
    }

    suspend fun getWorld(id: String): World? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds/$id",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            return Gson().fromJson(response[0], World::class.java)
        }

        return null
    }

    suspend fun getUsers(
        username: String,
        limit: Int,
        byProduct: Int,
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

        response?.let {
            val temp: ArrayList<LimitedUser> = users
            val json = Gson().fromJson(response[0], Users::class.java)
            json?.forEach { user ->
                temp.add(user)
            }

            getUsers(
                username,
                limit,
                byProduct,
                if (MathHelper.isWithinByProduct(offset + n, byProduct, limit)) {
                    byProduct
                } else {
                    n
                },
                offset + n,
                temp
            )
        }

        return users
    }

    suspend fun getFavoriteLimits(): FavoriteLimits? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/auth/user/favoritelimits",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            return Gson().fromJson(response[0], FavoriteLimits::class.java)
        }

        return null
    }

    suspend fun getFavoriteGroups(type: String): FavoriteGroups? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/favorite/groups?type=$type",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            return Gson().fromJson(response[0], FavoriteGroups::class.java)
        }

        return null
    }

    suspend fun addFavorite(
        type: String,
        id: String,
        tag: String
    ): FavoriteAdd? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val body = "{\"type\":\"$type\",\"favoriteId\":\"$id\",\"tags\":[\"$tag\"]}"

        val result = doRequest(
            method = "POST",
            url = "$apiBase/favorites",
            headers = headers,
            body = body
        )

        val response = handleRequest(result)

        response?.let {
            return Gson().fromJson(response[0], FavoriteAdd::class.java)
        }

        return null
    }

    suspend fun removeFavorite(
        id: String
    ): Boolean {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "DELETE",
            url = "$apiBase/favorites/$id",
            headers = headers,
            body = null
        )

        return result is Result.Succeeded
    }

    suspend fun updateFavorite(
        type: String,
        tag: String,
        displayName: String,
        visibility: String
    ): Boolean {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val body = "{\"displayName\":\"$displayName\",\"visibility\":\"$visibility\"}"

        val user = CacheManager.getProfile()?.id

        val result = doRequest(
            method = "PUT",
            url = "$apiBase/favorite/group/$type/$tag/$user",
            headers = headers,
            body = body
        )

        return result is Result.Succeeded
    }

    suspend fun getFavorites(
        type: String,
        tag: String,
        n: Int = 50,
        offset: Int = 0,
        favorites: ArrayList<Favorite> = arrayListOf()
    ): ArrayList<Favorite> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/favorites?type=$type&n=$n&offset=$offset&tag=$tag",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            val temp: ArrayList<Favorite> = favorites
            val json = Gson().fromJson(response[0], Favorites::class.java)
            json?.forEach { favorite ->
                temp.add(favorite)
            }

            getFavorites(type, tag, n, offset + n, temp)
        }

        return favorites
    }

    suspend fun getFavoriteAvatars(
        tag: String,
        n: Int = 50,
        offset: Int = 0,
        favorites: ArrayList<FavoriteAvatar> = arrayListOf()
    ): ArrayList<FavoriteAvatar> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/avatars/favorites?n=$n&offset=$offset&tag=$tag",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            val temp: ArrayList<FavoriteAvatar> = favorites
            val json = Gson().fromJson(response[0], FavoriteAvatars::class.java)
            json?.forEach { favorite ->
                temp.add(favorite)
            }

            getFavoriteAvatars(tag, n, offset + n, temp)
        }

        return favorites
    }

    suspend fun getFavoriteWorlds(
        tag: String,
        n: Int = 50,
        offset: Int = 0,
        favorites: ArrayList<FavoriteWorld> = arrayListOf()
    ): ArrayList<FavoriteWorld> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/worlds/favorites?n=$n&offset=$offset&tag=$tag",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            val temp: ArrayList<FavoriteWorld> = favorites
            val json = Gson().fromJson(response[0], FavoriteWorlds::class.java)
            json?.forEach { favorite ->
                temp.add(favorite)
            }

            getFavoriteWorlds(tag, n, offset + n, temp)
        }

        return favorites
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

        response?.let {
            return Gson().fromJson(response[0], Notifications::class.java)
        }

        return null
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

        response?.let {
            return Gson().fromJson(response[0], User::class.java)
        }

        return null
    }

    suspend fun getAvatar(avatarId: String): Avatar? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/avatars/${avatarId}",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            return Gson().fromJson(response[0], Avatar::class.java)
        }

        return null
    }

    suspend fun getOwnAvatars(
        n: Int = 50,
        offset: Int = 0,
        avatars: ArrayList<Avatar> = arrayListOf()
    ): ArrayList<Avatar> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/avatars?releaseStatus=all&sort=updated&order=descending&user=me&n=$n&offset=$offset",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            val temp: ArrayList<Avatar> = avatars
            val json = Gson().fromJson(response[0], Avatars::class.java)
            json?.forEach { avatar ->
                temp.add(avatar)
            }

            getOwnAvatars(n, offset + n, temp)
        }

        return avatars
    }

    suspend fun getGroups(
        query: String,
        limit: Int,
        byProduct: Int,
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

        response?.let {
            val temp: ArrayList<Group> = groups
            val json = Gson().fromJson(response[0], Groups::class.java)
            json?.forEach { group ->
                temp.add(group)
            }

            getGroups(
                query,
                limit,
                byProduct,
                if (MathHelper.isWithinByProduct(offset + n, byProduct, limit)) {
                    byProduct
                } else {
                    n
                },
                offset + n,
                groups
            )
        }

        return groups
    }

    suspend fun getUserGroups(
        userId: String
    ): ArrayList<UserGroups.Group> {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        val result = doRequest(
            method = "GET",
            url = "$apiBase/users/$userId/groups",
            headers = headers,
            body = null
        )

        val response = handleRequest(result)

        response?.let {
            val groups: ArrayList<UserGroups.Group> = arrayListOf()
            val json = Gson().fromJson(response[0], UserGroups::class.java)
            json?.forEach { group ->
                groups.add(group)
            }

            return groups
        }

        return arrayListOf() // <!-- UNREACHABLE
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

        response?.let {
            return Gson().fromJson(response[0], Group::class.java)
        }

        return null
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

        response?.let {
            return Gson().fromJson(response[0], GroupInstances::class.java)
        }

        return null
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

        response?.let {
            return Gson().fromJson(response[0], FileMetadata::class.java)
        }

        return null
    }

    suspend fun updateProfile(
        id: String,
        status: String,
        description: String,
        bio: String,
        bioLinks: List<String>
    ): User? {

        val headers = Headers.Builder()

        headers["User-Agent"] = userAgent

        // TODO: wrap inside object
        val body = "{\"status\":\"$status\",\"statusDescription\":\"$description\",\"bio\":\"${bio.replace("\n", "\\n")}\",\"bioLinks\":${Gson().toJson(bioLinks)}}"

        val result = doRequest(
            method = "PUT",
            url = "$apiBase/users/$id",
            headers = headers,
            body = body
        )

        val response = handleRequest(result)

        response?.let {
            return Gson().fromJson(response[0], User::class.java)
        }

        return null
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

        response?.let {
            return response[0].toInt()
        }

        return -1
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

        response?.let {
            return Gson().fromJson(response[0], SteamCount::class.java).response.playerCount
        }

        return -1
    }
}