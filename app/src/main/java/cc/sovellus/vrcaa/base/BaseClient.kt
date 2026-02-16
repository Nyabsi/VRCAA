/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.await
import cc.sovellus.vrcaa.helper.DnsHelper
import cc.sovellus.vrcaa.helper.TLSHelper
import cc.sovellus.vrcaa.manager.DebugManager
import okhttp3.ConnectionPool
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.http2.StreamResetException
import java.io.ByteArrayOutputStream
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
open class BaseClient {

    private val tlsHelper = TLSHelper()
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .dns(DnsHelper())
            .sslSocketFactory(tlsHelper.getSSLContext().socketFactory, tlsHelper.systemDefaultTrustManager())
            .addInterceptor { chain ->
                val original = chain.request()

                if (skipNextAuthorization.load()) {
                    skipNextAuthorization.exchange(false)
                    return@addInterceptor chain.proceed(original)
                }

                if (authorizationType == AuthorizationType.None || credentials.isEmpty()) {
                    return@addInterceptor chain.proceed(original)
                }

                val hasHeader = when (authorizationType) {
                    AuthorizationType.Cookie ->
                        original.header("Cookie") != null
                    AuthorizationType.Bearer ->
                        original.header("Authorization") != null
                    else -> false
                }
                if (hasHeader) {
                    return@addInterceptor chain.proceed(original)
                }

                val builder = original.newBuilder()
                when (authorizationType) {
                    AuthorizationType.Cookie ->
                        builder.addHeader("Cookie", credentials)
                    AuthorizationType.Bearer ->
                        builder.addHeader("Authorization", "Bearer $credentials")
                    else -> {}
                }

                chain.proceed(builder.build())
            }
            .build()
    }

    private lateinit var credentials: String
    private var authorizationType: AuthorizationType = AuthorizationType.None
    private var skipAuthNextFailure: AtomicBoolean = AtomicBoolean(false)
    private var skipNextAuthorization: AtomicBoolean = AtomicBoolean(false)

    // TODO: add new response types, when required.
    sealed class Result {
        data class Succeeded(val response: Response, val body: String) : Result()
        data class UnhandledResult(val response: Response) : Result()
        data object NoInternet : Result()
        data object RateLimited : Result()
        data class InvalidRequest(val body: String) : Result()
        data object Unauthorized : Result()
        data object NotFound : Result()
        data object InternalError : Result()
        data object UnknownMethod : Result()
        data object NotModified : Result()
        data object Forbidden : Result()
        data class GenericException(val exception: Throwable) : Result()
    }

    enum class AuthorizationType {
        None,
        Cookie,
        Bearer
    }

    private suspend fun handleRequest(
        response: Response,
        responseBody: String
    ): Result {
        return when (response.code) {
            200 -> Result.Succeeded(response, responseBody)
            304 -> Result.NotModified
            429 -> Result.RateLimited
            400 -> Result.InvalidRequest(responseBody)
            401 -> {
                if (skipAuthNextFailure.load()) {
                    skipAuthNextFailure.exchange(false)
                    return Result.Unauthorized
                }
                onAuthorizationFailure()
                Result.Unauthorized
            }
            403 -> Result.Forbidden
            404 -> Result.NotFound
            500 -> Result.InternalError
            else -> Result.UnhandledResult(response)
        }
    }

    suspend fun doRequest(
        method: String,
        url: String,
        headers: Headers,
        body: String?,
        retryAfterFailure: Boolean = true,
        ignoreAuthorization: Boolean = false,
        skipAuthorizationFailure: Boolean = false
    ): Result {

        val type: MediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody: RequestBody = body?.toRequestBody(type) ?: RequestBody.EMPTY

        if (ignoreAuthorization)
           skipNextAuthorization.exchange(true)
        if (skipAuthorizationFailure)
            skipAuthNextFailure.exchange(true)

        return try {
             when (method) {
                "GET" -> {

                    val request = Request.Builder()
                        .headers(headers = headers)
                        .url(url)
                        .get()
                        .build()

                    val response = client.newCall(request).await()
                    val responseBody = response.body.string()

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

                    val result = handleRequest(response, responseBody)

                    if (result == Result.Unauthorized && retryAfterFailure) {
                        return doRequest(
                            method = method,
                            url = url,
                            headers = headers,
                            body = body,
                            retryAfterFailure = false,
                            ignoreAuthorization = ignoreAuthorization,
                            skipAuthorizationFailure = skipAuthorizationFailure
                        )
                    }

                    return result
                }

                "POST" -> {
                    val request = Request.Builder()
                        .headers(headers = headers)
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

                    val result = handleRequest(response, responseBody)

                    if (result == Result.Unauthorized && retryAfterFailure) {
                        return doRequest(
                            method = method,
                            url = url,
                            headers = headers,
                            body = body,
                            retryAfterFailure = false,
                            ignoreAuthorization = ignoreAuthorization,
                            skipAuthorizationFailure = skipAuthorizationFailure
                        )
                    }

                    return result
                }

                "PUT" -> {
                    val request = Request.Builder()
                        .headers(headers = headers)
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

                    val result = handleRequest(response, responseBody)

                    if (result == Result.Unauthorized && retryAfterFailure) {
                        return doRequest(
                            method = method,
                            url = url,
                            headers = headers,
                            body = body,
                            retryAfterFailure = false,
                            ignoreAuthorization = ignoreAuthorization,
                            skipAuthorizationFailure = skipAuthorizationFailure
                        )
                    }

                    return result
                }

                "DELETE" -> {
                    val request = Request.Builder()
                        .headers(headers = headers)
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

                    val result = handleRequest(response, responseBody)

                    if (result == Result.Unauthorized && retryAfterFailure) {
                        return doRequest(
                            method = method,
                            url = url,
                            headers = headers,
                            body = body,
                            retryAfterFailure = false,
                            ignoreAuthorization = ignoreAuthorization,
                            skipAuthorizationFailure = skipAuthorizationFailure
                        )
                    }

                    return result
                }

                else -> {
                    Result.UnknownMethod
                }
            }
        } catch (_: UnknownHostException) {
            Result.NoInternet
        } catch (_: SocketException) {
            Result.NoInternet
        } catch (_: SocketTimeoutException) {
            Result.NoInternet
        } catch (_: StreamResetException) {
            Result.InternalError
        } catch (e: Throwable) {
            Result.GenericException(e)
        }
    }

    suspend fun doRequestUpload(
        context: Context,
        url: String,
        headers: Headers,
        fileUri: Uri,
        formFields: Map<String, String> = emptyMap(),
        addWhiteBorder: Boolean = false,
        retryAfterFailure: Boolean = true,
        ignoreAuthorization: Boolean = false,
        skipAuthorizationFailure: Boolean = false
    ): Result {

        if (ignoreAuthorization)
            skipNextAuthorization.exchange(true)
        if (skipAuthorizationFailure)
            skipAuthNextFailure.exchange(true)

        return try {
            val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

            for ((key, value) in formFields) {
                multipartBuilder.addFormDataPart(key, value)
            }

            val inputStream = context.contentResolver.openInputStream(fileUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val isSquare = formFields["maskTag"] == "square"

            val normalizedBitmap = if (!isSquare && bitmap.height > bitmap.width) {
                val matrix = Matrix().apply { postRotate(90f) }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {
                bitmap
            }

            val processedBitmap = if (isSquare) {
                val size = minOf(normalizedBitmap.width, normalizedBitmap.height)
                val x = (normalizedBitmap.width - size) / 2
                val y = (normalizedBitmap.height - size) / 2
                val square = Bitmap.createBitmap(normalizedBitmap, x, y, size, size)

                val targetSize = when {
                    size >= 1024 -> 1024
                    size >= 512 -> 512
                    size >= 256 -> 256
                    else -> 128
                }

                square.scale(targetSize, targetSize)
            } else {
                normalizedBitmap.scale(1440, 1080)
            }

            val finalBitmap = if (addWhiteBorder) {
                val bitmapWithBorder = createBitmap(2048, 1440)
                val canvas = Canvas(bitmapWithBorder)
                canvas.drawColor(Color.WHITE)

                val xBorderOffset =  (2048 - processedBitmap.width) / 2
                val yBorderOffset = 69
                val dstRect = Rect(
                    xBorderOffset,
                    yBorderOffset,
                    xBorderOffset + processedBitmap.width,
                    yBorderOffset + processedBitmap.height
                )

                canvas.drawBitmap(processedBitmap, null, dstRect, null)

                bitmapWithBorder
            } else {
                processedBitmap
            }

            val stream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
            val bytes = stream.toByteArray()

            multipartBuilder.addFormDataPart(
                "file",
                "blob",
                bytes.toRequestBody("image/jpeg".toMediaType())
            )

            normalizedBitmap.recycle()
            processedBitmap.recycle()
            finalBitmap.recycle()

            val request = Request.Builder()
                .headers(headers)
                .url(url)
                .post(multipartBuilder.build())
                .build()

            val response = client.newCall(request).await()
            val responseBody = response.body?.string().orEmpty()

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

            val result = handleRequest(response, responseBody)

            if (result == Result.Unauthorized && retryAfterFailure) {
                return doRequestUpload(
                    context = context,
                    url = url,
                    headers = headers,
                    fileUri = fileUri,
                    formFields = formFields,
                    retryAfterFailure = false,
                    ignoreAuthorization = ignoreAuthorization,
                    skipAuthorizationFailure = skipAuthorizationFailure
                )
            }

            return result

        } catch (_: UnknownHostException) {
            Result.NoInternet
        } catch (_: SocketException) {
            Result.NoInternet
        } catch (_: SocketTimeoutException) {
            Result.NoInternet
        } catch (_: StreamResetException) {
            Result.InternalError
        } catch (e: Throwable) {
            Result.GenericException(e)
        }
    }

    protected open suspend fun onAuthorizationFailure() { }

    fun setAuthorization(type: AuthorizationType, credentials: String) {
        this.credentials = credentials
        this.authorizationType = type
    }
}
