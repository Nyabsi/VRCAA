package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.User

interface IAuth {

    enum class AuthType {
        AUTH_NONE,
        AUTH_TOTP,
        AUTH_OTP, // <!-- UNSUPPORTED
        AUTH_EMAIL
    }

    data class AuthResult(val success: Boolean, val authType: AuthType = AuthType.AUTH_NONE)

    suspend fun login(username: String, password: String): AuthResult
    suspend fun verify(type: AuthType, code: String): AuthResult
    suspend fun logout(): Boolean

    suspend fun fetchToken(): String?
    suspend fun fetchCurrentUser(): User?
}