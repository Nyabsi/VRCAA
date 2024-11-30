package cc.sovellus.vrcaa.api

import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.manager.DebugManager
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
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseClient {
    /* inherited classes don't need to access the client variable */
    private val client: OkHttpClient by lazy { OkHttpClient() }

    private lateinit var credentials: String
    private var authorizationType: AuthorizationType = AuthorizationType.None
    private var shouldIgnoreRequest: Boolean = false

    // TODO: add new response types, when required.
    sealed class Result {
        data class Succeeded(val response: Response, val body: String) : Result()
        data class UnhandledResult(val response: Response) : Result()
        data object NoInternet : Result()
        data object RateLimited : Result()
        data object InvalidRequest : Result()
        data object Unauthorized : Result()
        data object InternalError : Result()
        data object UnknownMethod : Result()
        data object NotModified : Result()
        data object Ignored : Result()
    }

    enum class AuthorizationType {
        None,
        Cookie,
        Bearer
    }

    private fun handleRequest(
        response: Response,
        responseBody: String
    ): Result = when (response.code) {
        200 -> {
            if (shouldIgnoreRequest)
                shouldIgnoreRequest = false
            Result.Succeeded(response, responseBody)
        }
        304 -> Result.NotModified
        429 -> Result.RateLimited
        400 -> Result.InvalidRequest
        401 -> {
            shouldIgnoreRequest = true
            Result.Unauthorized
        }
        500 -> Result.InternalError
        else -> Result.UnhandledResult(response)
    }

    suspend fun doRequest(
        method: String,
        url: String,
        headers: Headers.Builder,
        body: String?,
        ignoreAuthorization: Boolean = false,
        bypassIgnore: Boolean = false
    ): Result {

        if (shouldIgnoreRequest && !bypassIgnore)
            return Result.Ignored

        val type: MediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody: RequestBody = body?.toRequestBody(type) ?: EMPTY_REQUEST

        if (!ignoreAuthorization) {
            when (authorizationType) {
                AuthorizationType.Cookie -> {
                    headers["Cookie"] = credentials
                }
                AuthorizationType.Bearer -> {
                    headers["Authorization"] = "Bearer $credentials"
                }
                else -> {}
            }
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
                    val responseBody = response.body?.string().toString()

                    if (App.isNetworkLoggingEnabled()) {
                        DebugManager.addDebugMetadata(
                            DebugManager.DebugMetadataData(
                                type = DebugManager.DebugType.DEBUG_TYPE_HTTP,
                                url = url,
                                methodType = "GET",
                                code = response.code,
                                payload = responseBody
                            )
                        )
                    }

                    handleRequest(response, responseBody)
                }

                "POST" -> {
                    val request = Request.Builder()
                        .headers(headers = finalHeaders)
                        .url(url)
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).await()
                    val responseBody = response.body?.string().toString()

                    if (App.isNetworkLoggingEnabled()) {
                        DebugManager.addDebugMetadata(
                            DebugManager.DebugMetadataData(
                                type = DebugManager.DebugType.DEBUG_TYPE_HTTP,
                                url = url,
                                methodType = "POST",
                                code = response.code,
                                payload = responseBody
                            )
                        )
                    }

                    handleRequest(response, responseBody)
                }

                "PUT" -> {
                    val request = Request.Builder()
                        .headers(headers = finalHeaders)
                        .url(url)
                        .put(requestBody)
                        .build()

                    val response = client.newCall(request).await()
                    val responseBody = response.body?.string().toString()

                    if (App.isNetworkLoggingEnabled()) {
                        DebugManager.addDebugMetadata(
                            DebugManager.DebugMetadataData(
                                type = DebugManager.DebugType.DEBUG_TYPE_HTTP,
                                url = url,
                                methodType = "PUT",
                                code = response.code,
                                payload = responseBody
                            )
                        )
                    }

                    handleRequest(response, responseBody)
                }

                "DELETE" -> {
                    val request = Request.Builder()
                        .headers(headers = finalHeaders)
                        .url(url)
                        .delete(requestBody)
                        .build()

                    val response = client.newCall(request).await()
                    val responseBody = response.body?.string().toString()

                    if (App.isNetworkLoggingEnabled()) {
                        DebugManager.addDebugMetadata(
                            DebugManager.DebugMetadataData(
                                type = DebugManager.DebugType.DEBUG_TYPE_HTTP,
                                url = url,
                                methodType = "DELETE",
                                code = response.code,
                                payload = responseBody
                            )
                        )
                    }

                    handleRequest(response, responseBody)
                }

                else -> {
                    Result.UnknownMethod
                }
            }
        } catch (e: UnknownHostException) {
            Result.NoInternet
        } catch (e: SocketException) {
            Result.NoInternet
        } catch (e: SocketTimeoutException) {
            Result.NoInternet
        }
    }

    fun setAuthorization(type: AuthorizationType, credentials: String) {
        this.credentials = credentials
        this.authorizationType = type
    }
}