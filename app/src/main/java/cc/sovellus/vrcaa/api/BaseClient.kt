package cc.sovellus.vrcaa.api

import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.gildor.coroutines.okhttp.await

open class BaseClient {
    /* inherited classes don't need to access the client variable */
    private val client: OkHttpClient by lazy { OkHttpClient() }

    private lateinit var credentials: String
    private var authorizationType: AuthorizationType = AuthorizationType.None

    // TODO: add new response types, when required.
    sealed class Result {
        data class Succeeded(val response: Response, val body: String) : Result()
        data class UnhandledResult(val response: Response) : Result()
        data class ClientExceptionResult(val response: String) : Result()
        data object RateLimited : Result()
        data object InvalidRequest : Result()
        data object Unauthorized : Result()
        data object InternalError : Result()
        data object UnknownMethod : Result()
        data object NotModified : Result()
    }

    enum class AuthorizationType {
        None,
        Cookie,
        Bearer
    }

    private fun handleRequest(
        response: Response
    ): Result = when (response.code) {
        200 -> Result.Succeeded(response, response.body?.string().toString())
        304 -> Result.NotModified
        429 -> Result.RateLimited
        400 -> Result.InvalidRequest
        401 -> Result.Unauthorized
        500 -> Result.InternalError
        else -> Result.UnhandledResult(response)
    }

    suspend fun doRequest(
        method: String,
        url: String,
        headers: Headers.Builder,
        body: String?,
    ): Result {

        val type: MediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody: RequestBody = body?.toRequestBody(type) ?: EMPTY_REQUEST

        when (authorizationType) {
            AuthorizationType.Cookie -> {
                headers["Cookie"] = credentials
            }
            AuthorizationType.Bearer -> {
                headers["Authorization"] = "Bearer $credentials"
            }
            else -> {}
        }

        val finalHeaders = headers.build()

        return try {
             when (method) {
                "GET" -> {
                    val request = Request.Builder()
                        .headers(headers = finalHeaders)
                        .url(url)
                        .get()
                        .build()

                    val response = client.newCall(request).await()
                    handleRequest(response)
                }

                "POST" -> {
                    val request = Request.Builder()
                        .headers(headers = finalHeaders)
                        .url(url)
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).await()
                    handleRequest(response)
                }

                "PUT" -> {
                    val request = Request.Builder()
                        .headers(headers = finalHeaders)
                        .url(url)
                        .put(requestBody)
                        .build()

                    val response = client.newCall(request).await()
                    handleRequest(response)
                }

                "DELETE" -> {
                    val request = Request.Builder()
                        .headers(headers = finalHeaders)
                        .url(url)
                        .delete(requestBody)
                        .build()

                    val response = client.newCall(request).await()
                    handleRequest(response)
                }

                else -> {
                    Result.UnknownMethod
                }
            }
        } catch (e: Exception) {
            Result.ClientExceptionResult(e.message.toString())
        }
    }

    fun setAuthorization(type: AuthorizationType, credentials: String) {
        this.credentials = credentials
        this.authorizationType = type
    }
}