package cc.sovellus.vrcaa.api.vrchat.http

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.os.bundleOf
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.BaseClient
import cc.sovellus.vrcaa.api.vrchat.Config
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IAuth
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IAuth.AuthType
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IAvatars
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFiles
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFriends
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IGroups
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IInstances
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IUser
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IUsers
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IWorlds
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatars
import cc.sovellus.vrcaa.api.vrchat.http.models.Code
import cc.sovellus.vrcaa.api.vrchat.http.models.ErrorResponse
import cc.sovellus.vrcaa.api.vrchat.http.models.Favorite
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteAdd
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteAvatar
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteAvatars
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteBody
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteGroups
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteLimits
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteWorld
import cc.sovellus.vrcaa.api.vrchat.http.models.FavoriteWorlds
import cc.sovellus.vrcaa.api.vrchat.http.models.Favorites
import cc.sovellus.vrcaa.api.vrchat.http.models.FileMetadata
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.http.models.Friends
import cc.sovellus.vrcaa.api.vrchat.http.models.Group
import cc.sovellus.vrcaa.api.vrchat.http.models.GroupInstance
import cc.sovellus.vrcaa.api.vrchat.http.models.GroupInstances
import cc.sovellus.vrcaa.api.vrchat.http.models.Groups
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.InstanceCreateBody
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.ProfileUpdate
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.api.vrchat.http.models.UserGroup
import cc.sovellus.vrcaa.api.vrchat.http.models.UserGroups
import cc.sovellus.vrcaa.api.vrchat.http.models.Users
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.api.vrchat.http.models.Worlds
import cc.sovellus.vrcaa.api.vrchat.models.AuthResponse
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.extension.userCredentials
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.service.PipelineService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import okhttp3.Headers
import kotlin.coroutines.CoroutineContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class HttpClient : BaseClient(), CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
    private var listener: SessionListener? = null
    private var authenticationCounter: Int = 0

    init {
        setAuthorization(AuthorizationType.Cookie, "${preferences.authToken} ${preferences.twoFactorToken}")
    }

    interface SessionListener {
        fun onSessionInvalidate()
        fun noInternet()
    }

    fun setSessionListener(listener: SessionListener) {
        this.listener = listener
    }

    private fun handleExceptions(result: Result) {
        when (result) {
            Result.InternalError -> {
                if (BuildConfig.DEBUG)
                    throw RuntimeException("VRChat returned INTERNAL ERROR 500, please check the API query for invalid parameters.")
            }
            Result.NoInternet -> {
                listener?.noInternet()
            }
            Result.RateLimited -> {

                if (BuildConfig.DEBUG)
                    throw RuntimeException("You're doing actions too quick! Please calm down.")

                launch {
                    Toast.makeText(
                        context,
                        "You're doing actions too quick! Please calm down.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Result.Unauthorized -> {
                setAuthorization(AuthorizationType.Cookie, preferences.twoFactorToken)

                launch {
                    // prevent duplicate requests in event of failure
                    if (preferences.authToken.isNotEmpty()) {
                        preferences.authToken = ""

                        val response = api.auth.login(
                            preferences.userCredentials.first,
                            preferences.userCredentials.second
                        )

                        if (response.success && response.authType == AuthType.AUTH_NONE) {
                            val intent = Intent(App.getContext(), PipelineService::class.java)
                            App.getContext().stopService(intent)

                            val bundle = bundleOf()
                            bundle.putBoolean("SKIP_INIT_CACHE", true)

                            intent.putExtras(bundle)
                            App.getContext().startService(intent)
                        } else {
                            listener?.onSessionInvalidate()
                        }
                    }
                }
            }
            Result.UnknownMethod -> {
                if (BuildConfig.DEBUG)
                    throw RuntimeException("Invalid method used for request, make sure you're using a supported method.")
            }
            is Result.InvalidRequest -> {

                val reason = Gson().fromJson(result.body, ErrorResponse::class.java).error.message

                launch {
                    Toast.makeText(
                        context,
                        "API returned (400): $reason",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> { /* Stub! */ }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    val auth = object : IAuth {
        override suspend fun login(username: String, password: String): IAuth.AuthResult {

            preferences.userCredentials = Pair(username, password)

            val token = Base64.encode(("${UrlEncoderUtil.encode(username)}:${UrlEncoderUtil.encode(password)}").toByteArray())

            val headers = Headers.Builder()
                .add("Authorization", "Basic $token")
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/auth/user",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    val cookies = result.response.headers("Set-Cookie")
                    if (cookies.isNotEmpty()) {
                        if (result.body.contains("emailOtp")) {
                            preferences.authToken = cookies[0]
                            setAuthorization(AuthorizationType.Cookie, preferences.authToken)
                            return IAuth.AuthResult(true, "", AuthType.AUTH_EMAIL)
                        }

                        if (result.body.contains("totp")) {
                            preferences.authToken = cookies[0]
                            setAuthorization(AuthorizationType.Cookie, preferences.authToken)
                            return IAuth.AuthResult(true, "", AuthType.AUTH_TOTP)
                        }

                        preferences.authToken = cookies[0]
                        setAuthorization(AuthorizationType.Cookie,"${preferences.authToken} ${preferences.twoFactorToken}")
                        return IAuth.AuthResult(true)
                    }

                    // if server doesn't send cookies, it means we're already authenticated.
                    // I don't know how can you reach this statement though.
                    return IAuth.AuthResult(true)
                }
                is Result.Unauthorized -> {
                    return IAuth.AuthResult(false, context.getString(R.string.login_toast_wrong_credentials))
                }
                is Result.NoInternet -> {
                    return IAuth.AuthResult(false, "Login Failed: No internet connection.")
                }
                is Result.RateLimited -> {
                    return IAuth.AuthResult(false, "Login Failed: You're logging too quickly!")
                }
                else -> {
                    handleExceptions(result)
                    return IAuth.AuthResult(false, "Login Failed: Unknown exception from server.")
                }
            }
        }

        override suspend fun verify(type: IAuth.AuthType, code: String): IAuth.AuthResult {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val dParameter = when (type) {
                IAuth.AuthType.AUTH_EMAIL -> "emailotp"
                IAuth.AuthType.AUTH_TOTP -> "totp"
                else -> { "" }
            }

            val result = doRequest(
                method = "POST",
                url = "${Config.API_BASE_URL}/auth/twofactorauth/${dParameter}/verify",
                headers = headers,
                body =  Gson().toJson(Code(code))
            )

            when (result) {
                is Result.Succeeded -> {
                    val cookies = result.response.headers("Set-Cookie")
                    preferences.twoFactorToken = cookies[0]

                    setAuthorization(AuthorizationType.Cookie, "${preferences.authToken} ${preferences.twoFactorToken}")
                    return IAuth.AuthResult(true)
                }
                is Result.Unauthorized -> {
                    return IAuth.AuthResult(false)
                }
                else -> {
                    handleExceptions(result)
                    return IAuth.AuthResult(false)
                }
            }
        }

        override suspend fun logout(): Boolean {
            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "PUT",
                url = "${Config.API_BASE_URL}/logout",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }

        override suspend fun fetchToken(): String? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/auth",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, AuthResponse::class.java).token
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun fetchCurrentUser(): User? {
            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/auth/user",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, User::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }
    }

    val friends = object : IFriends {

        override suspend fun fetchFriends(
            offline: Boolean,
            n: Int,
            offset: Int,
            friends: ArrayList<Friend>
        ): ArrayList<Friend> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/auth/user/friends?offline=${offline}&n=${n}&offset=${offset}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return friends

                    val json = Gson().fromJson(result.body, Friends::class.java)
                    json?.forEach { friend ->
                        friends.add(friend)
                    }

                    fetchFriends(offline, n, offset + n, friends)
                }
                is Result.NotModified -> {
                    return friends
                }
                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }
    }

    val users = object : IUsers {

        override suspend fun fetchUserByUserId(userId: String): LimitedUser? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/users/${userId}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, LimitedUser::class.java)
                }
                is Result.Unauthorized -> {
                    handleExceptions(result)
                    return fetchUserByUserId(userId)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun fetchUsersByName(
            query: String,
            n: Int,
            offset: Int
        ): ArrayList<LimitedUser> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/users?search=${query}&n=${n}&offset=${offset}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return arrayListOf()

                    val users: ArrayList<LimitedUser> = arrayListOf()
                    val json = Gson().fromJson(result.body, Users::class.java)
                    json?.forEach { user ->
                        users.add(user)
                    }

                    users
                }
                is Result.NotModified -> {
                    arrayListOf()
                }
                is Result.Forbidden -> {
                    arrayListOf()
                }
                else -> {
                    handleExceptions(result)
                    arrayListOf()
                }
            }
        }

        override suspend fun fetchGroupsByUserId(userId: String): ArrayList<UserGroup> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/users/${userId}/groups",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, UserGroups::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }
    }

    val instances = object : IInstances {

        override suspend fun fetchInstance(intent: String): Instance? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/instances/${intent}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, Instance::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun selfInvite(intent: String): Boolean {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "POST",
                url = "${Config.API_BASE_URL}/invite/myself/to/${intent}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                is Result.NotFound -> {
                    return false
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }

        override suspend fun fetchGroupInstancesById(groupId: String): ArrayList<GroupInstance> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/groups/${groupId}/instances",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, GroupInstances::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }

        override suspend fun createInstance(
            worldId: String,
            type: IInstances.InstanceType,
            region: IInstances.InstanceRegion,
            ownerId: String?,
            canRequestInvite: Boolean
        ): Instance? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val body = Gson().toJson(InstanceCreateBody(
                worldId = worldId,
                type = type.toString(),
                region = region.toString(),
                ownerId = ownerId,
                canRequestInvite = canRequestInvite
            ))

            val result = doRequest(
                method = "POST",
                url = "${Config.API_BASE_URL}/instances",
                headers = headers,
                body = body
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, Instance::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }
    }

    val worlds = object : IWorlds {

        override suspend fun fetchRecent(): ArrayList<World> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/worlds/recent?featured=false",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, Worlds::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }

        override suspend fun fetchWorldsByName(
            query: String,
            sort: String,
            n: Int,
            offset: Int
        ): ArrayList<World> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/worlds?featured=false&n=${n}&sort=${sort}&search=${query}&offset=${offset}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                         return arrayListOf()

                    val worlds: ArrayList<World> = arrayListOf()
                    val json = Gson().fromJson(result.body, Worlds::class.java)
                    json?.forEach { world ->
                        worlds.add(world)
                    }

                    worlds
                }
                else -> {
                    handleExceptions(result)
                    arrayListOf()
                }
            }
        }

        override suspend fun fetchWorldsByAuthorId(
            userId: String,
            private: Boolean,
            n: Int,
            offset: Int,
            worlds: ArrayList<World>
        ): ArrayList<World> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val releaseStatus = if (private) {
                "all"
            } else {
                "public"
            }

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/worlds?releaseStatus=${releaseStatus}&sort=updated&order=descending&userId=${userId}&n=${n}&offset=${offset}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return worlds

                    val json = Gson().fromJson(
                        result.body,
                        Worlds::class.java
                    )
                    json?.forEach { world ->
                        worlds.add(world)
                    }

                    fetchWorldsByAuthorId(userId, private, n, offset + n, worlds)
                }
                is Result.NotModified -> {
                    return worlds
                }
                is Result.Forbidden -> {
                    return worlds
                }
                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }

        override suspend fun fetchWorldByWorldId(worldId: String): World? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/worlds/${worldId}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, World::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }
    }

    val favorites = object: IFavorites {

        override suspend fun fetchLimits(): FavoriteLimits? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/auth/user/favoritelimits",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, FavoriteLimits::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun fetchFavoriteGroups(type: FavoriteType): FavoriteGroups? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val dTypeString = when (type) {
                FavoriteType.FAVORITE_WORLD -> "world"
                FavoriteType.FAVORITE_AVATAR -> "avatar"
                FavoriteType.FAVORITE_FRIEND -> "friend"
                FavoriteType.FAVORITE_NONE -> ""
            }

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/favorite/groups?type=${dTypeString}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, FavoriteGroups::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun addFavorite(
            type: FavoriteType,
            favoriteId: String,
            tag: String
        ): FavoriteAdd? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val dTypeString = when (type) {
                FavoriteType.FAVORITE_WORLD -> "world"
                FavoriteType.FAVORITE_AVATAR -> "avatar"
                FavoriteType.FAVORITE_FRIEND -> "friend"
                FavoriteType.FAVORITE_NONE -> ""
            }

            val body = Gson().toJson(FavoriteBody(dTypeString, favoriteId, arrayListOf(tag)))

            val result = doRequest(
                method = "POST",
                url = "${Config.API_BASE_URL}/favorites",
                headers = headers,
                body = body
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, FavoriteAdd::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun removeFavorite(favoriteId: String): Boolean {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "DELETE",
                url = "${Config.API_BASE_URL}/favorites/${favoriteId}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                is Result.NotFound -> {
                    return false
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }

        override suspend fun updateFavoriteGroup(
            type: FavoriteType,
            tag: String,
            newDisplayName: String,
            newVisibility: String
        ): Boolean {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val body = "{\"displayName\":\"$newDisplayName\",\"visibility\":\"$newVisibility\"}"

            val user = CacheManager.getProfile()?.id

            val dTypeString = when (type) {
                FavoriteType.FAVORITE_WORLD -> "world"
                FavoriteType.FAVORITE_AVATAR -> "avatar"
                FavoriteType.FAVORITE_FRIEND -> "friend"
                FavoriteType.FAVORITE_NONE -> ""
            }

            val result = doRequest(
                method = "PUT",
                url = "${Config.API_BASE_URL}/favorite/group/${dTypeString}/${tag}/${user}",
                headers = headers,
                body = body
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                is Result.NotFound -> {
                    return false
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }

        override suspend fun fetchFavorites(
            type: FavoriteType,
            tag: String,
            n: Int,
            offset: Int,
            favorites: ArrayList<Favorite>
        ): ArrayList<Favorite> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val dTypeString = when (type) {
                FavoriteType.FAVORITE_WORLD -> "world"
                FavoriteType.FAVORITE_AVATAR -> "avatar"
                FavoriteType.FAVORITE_FRIEND -> "friend"
                FavoriteType.FAVORITE_NONE -> ""
            }

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/favorites?type=${dTypeString}&n=${n}&offset=${offset}&tag=${tag}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return favorites

                    val json = Gson().fromJson(result.body, Favorites::class.java)

                    json?.forEach { favorite ->
                        favorites.add(favorite)
                    }

                    fetchFavorites(type, tag, n, offset + n, favorites)
                }

                is Result.NotModified -> {
                    return favorites
                }

                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }

        override suspend fun fetchFavoriteAvatars(
            tag: String,
            n: Int,
            offset: Int,
            favorites: ArrayList<FavoriteAvatar>
        ): ArrayList<FavoriteAvatar> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/avatars/favorites?n=${n}&offset=${offset}&tag=${tag}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return favorites

                    val json = Gson().fromJson(result.body, FavoriteAvatars::class.java)

                    json?.forEach { favorite ->
                        favorites.add(favorite)
                    }

                    fetchFavoriteAvatars(tag, n, offset + n, favorites)
                }

                is Result.NotModified -> {
                    return favorites
                }

                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }

        override suspend fun fetchFavoriteWorlds(
            tag: String,
            n: Int,
            offset: Int,
            favorites: ArrayList<FavoriteWorld>
        ): ArrayList<FavoriteWorld> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/worlds/favorites?n=${n}&offset=${offset}&tag=${tag}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return favorites

                    val json = Gson().fromJson(result.body, FavoriteWorlds::class.java)

                    json?.forEach { favorite ->
                        favorites.add(favorite)
                    }

                    fetchFavoriteWorlds(tag, n, offset + n, favorites)
                }

                is Result.NotModified -> {
                    return favorites
                }

                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }
    }

    val avatars = object : IAvatars {

        override suspend fun selectAvatarById(avatarId: String): Boolean {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "PUT",
                url = "${Config.API_BASE_URL}/avatars/${avatarId}/select",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                is Result.NotFound -> {
                    return false
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }

        override suspend fun fetchAvatarById(avatarId: String): Avatar? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/avatars/${avatarId}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, Avatar::class.java)
                }
                is Result.NotFound -> {
                    return null
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }
    }

    val groups = object : IGroups {

        override suspend fun fetchGroupsByName(
            query: String,
            n: Int,
            offset: Int
        ): ArrayList<Group> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/groups?query=${query}&n=${n}&offset=${offset}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return arrayListOf()

                    val groups: ArrayList<Group> = arrayListOf()
                    val json = Gson().fromJson(result.body, Groups::class.java)

                    json?.forEach { group ->
                        groups.add(group)
                    }

                    groups
                }
                is Result.NotModified -> {
                    arrayListOf()
                }
                is Result.Forbidden -> {
                    arrayListOf()
                }
                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }

        override suspend fun fetchGroupByGroupId(groupId: String): Group? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/groups/${groupId}?includeRoles=true&purpose=group",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, Group::class.java)
                }
                is Result.NotFound -> {
                    return null
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun joinGroupByGroupId(groupId: String): Boolean {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "POST",
                url = "${Config.API_BASE_URL}/groups/${groupId}/join?confirmOverrideBlock=false",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                is Result.NotFound -> {
                    return false
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }

        override suspend fun leaveGroupByGroupId(groupId: String): Boolean {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "POST",
                url = "${Config.API_BASE_URL}/groups/${groupId}/leave",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                is Result.NotFound -> {
                    return false
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }

        override suspend fun withdrawRequestByGroupId(groupId: String): Boolean {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "DELETE",
                url = "${Config.API_BASE_URL}/groups/${groupId}/requests",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return true
                }
                is Result.NotFound -> {
                    return false
                }
                else -> {
                    handleExceptions(result)
                    return false
                }
            }
        }
    }

    val files = object : IFiles {

        override suspend fun fetchMetadataByFileId(fileId: String): FileMetadata? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/file/${fileId}",
                headers = headers,
                body = null
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, FileMetadata::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }
    }

    val user = object : IUser {
        override suspend fun updateProfileByUserId(
            userId: String,
            newStatus: String,
            newDescription: String,
            newBio: String,
            newBioLinks: List<String>,
            newPronouns: String,
            newAgeVerificationStatus: String?
        ): User? {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val update = ProfileUpdate(
                ageVerificationStatus = newAgeVerificationStatus,
                bio = newBio,
                bioLinks = newBioLinks,
                status = newStatus,
                statusDescription = newDescription,
                pronouns = newPronouns
            )

            val result = doRequest(
                method = "PUT",
                url = "${Config.API_BASE_URL}/users/${userId}",
                headers = headers,
                body = Gson().toJson(update, ProfileUpdate::class.java)
            )

            when (result) {
                is Result.Succeeded -> {
                    return Gson().fromJson(result.body, User::class.java)
                }
                else -> {
                    handleExceptions(result)
                    return null
                }
            }
        }

        override suspend fun fetchOwnedAvatars(
            n: Int,
            offset: Int,
            avatars: ArrayList<Avatar>
        ): ArrayList<Avatar> {

            val headers = Headers.Builder()
                .add("User-Agent", Config.API_USER_AGENT)

            val result = doRequest(
                method = "GET",
                url = "${Config.API_BASE_URL}/avatars?releaseStatus=all&sort=updated&order=descending&user=me&n=${n}&offset=${offset}",
                headers = headers,
                body = null
            )

            return when (result) {
                is Result.Succeeded -> {
                    if (result.body == "[]")
                        return avatars

                    val json = Gson().fromJson(result.body, Avatars::class.java)

                    json?.forEach { avatar ->
                        avatars.add(avatar)
                    }

                    fetchOwnedAvatars(n, offset + n, avatars)
                }
                is Result.NotModified -> {
                    return avatars
                }
                else -> {
                    handleExceptions(result)
                    return arrayListOf()
                }
            }
        }
    }
}