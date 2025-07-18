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

package cc.sovellus.vrcaa.api.discord

import android.util.ArrayMap
import android.util.Log
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.api.discord.models.websocket.Hello
import cc.sovellus.vrcaa.api.discord.models.websocket.Incoming
import cc.sovellus.vrcaa.api.discord.models.websocket.Ready
import cc.sovellus.vrcaa.helper.StatusHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream

class GatewaySocket {

    private var socket: WebSocket? = null
    private val client: OkHttpClient by lazy { OkHttpClient() }
    private val gson = GsonBuilder().serializeNulls().create()
    private val inflater = Inflater()

    // NOTE: real client actually encodes the data into etf before sending, I haven't done that yet.
    // NOTE 2: real client uses zstd-stream instead of zlib-stream nowadays
    private var currentGatewayUrl = "wss://gateway.discord.gg/?encoding=json&v=9&compress=zlib-stream"

    private var sequence: Int = 0
    private var interval: Long = 0
    private var sessionId = ""
    private var shouldResume: Boolean = false
    private lateinit var schedule: ScheduledFuture<*>

    private var worldInfo: String = ""
    private var worldName: String = ""
    private var worldUrl: String = ""
    private var worldId: String = ""

    private var sessionStartTime: Long = 0

    private lateinit var mp: DiscordMediaProxy
    private var token: String = ""
    private var webHookUrl: String = ""

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

    private val heartbeatRunnable = Runnable {
        sendHeartbeat()
    }

    private val listener by lazy {
        object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket, response: Response
            ) {
                if (shouldResume) {
                    sendResume()
                } else {
                    sendIdentity()
                }
            }

            override fun onMessage(
                webSocket: WebSocket, bytes: ByteString
            ) {
                val uncompressedStream = ByteArrayOutputStream()
                InflaterOutputStream(uncompressedStream, inflater).write(bytes.toByteArray())

                val payload = Gson().fromJson(uncompressedStream.toString(), Incoming::class.java)

                if (payload.s!= null) {
                    sequence = payload.s
                }

                val opcode = Opcodes.toOp(payload.op)
                when(opcode) {
                    Opcodes.DISPATCH -> {
                        handleDispatch(payload)
                    }
                    Opcodes.HELLO -> {
                        val hello = Gson().fromJson(payload.d, Hello::class.java)
                        interval = hello.heartbeatInterval
                        schedule = scheduler.scheduleWithFixedDelay(heartbeatRunnable, interval, interval, TimeUnit.MILLISECONDS)
                    }
                    Opcodes.HEARTBEAT_ACK -> {
                        scheduler.schedule(heartbeatRunnable, interval, TimeUnit.MILLISECONDS)
                    }
                    else -> {
                        Log.d("VRCAA", "server sent unknown op: ${payload.op}")
                    }
               }
            }

            override fun onClosing(
                webSocket: WebSocket, code: Int, reason: String
            ) {
                if (code == 4000) {
                    shouldResume = true
                    connect()
                }
            }

            override fun onClosed(
                webSocket: WebSocket, code: Int, reason: String
            ) {
                if (code == 4000) {
                    shouldResume = true
                    connect()
                }
            }

            override fun onFailure(
                webSocket: WebSocket, t: Throwable, response: Response?
            ) {
                Log.d("VRCAA", "response error is ${t.message}")
            }
        }
    }

    // TODO: check if it was called, before connect.
    fun setParams(newToken: String, newWebHookUrl: String) {
        token = newToken
        webHookUrl = newWebHookUrl

        if (newWebHookUrl.isNotEmpty())
            mp = DiscordMediaProxy(webHookUrl)
    }

    fun connect() {
        val headers = Headers.Builder()
        headers["User-Agent"] = USER_AGENT

        val request = Request.Builder()
            .url(url = currentGatewayUrl)
            .headers(headers.build())
            .build()

        socket = client.newWebSocket(request, listener)
    }

    fun disconnect() {

        val presence = ArrayMap<String, Any?>()
        presence["status"] = "idle"
        presence["since"] = 0
        presence["activities"] = arrayOf<Any>()
        presence["afk"] = false

        val presencePayload = ArrayMap<String, Any>()
        presencePayload["op"] = 3
        presencePayload["d"] = presence

        socket?.send(gson.toJson(presencePayload))

        socket?.close(1000, "Closed by user")
        schedule.cancel(true)
    }

    private fun handleDispatch(payload: Incoming) {
        when (payload.t as String) {
            "READY" -> {
                val ready = Gson().fromJson(payload.d, Ready::class.java)
                sessionId = ready.sessionId
                currentGatewayUrl = "${ready.resumeGatewayUrl}/?encoding=json&v=9&compress=zlib-stream"

                if (BuildConfig.DEBUG)
                    Log.d("VRCAA", "Logged in discord with sessionId: $sessionId !")
            }
            else -> {
                Log.d("VRCAA", "server sent unknown dispatch: ${payload.t}")
            }
        }
    }

    suspend fun sendPresence(name: String?, info: String?, url: String?, id: String?, status: StatusHelper.Status) {

        val assets = ArrayMap<String, String>()

        if (name != null) {
            this.worldName = name
        }

        if (info != null) {
            this.worldInfo = info
        }

        if (url != null) {
            this.worldUrl = url
        }

        if (id != null) {
            this.worldId = id
        }

        if (sessionStartTime.toInt() == 0) {
            sessionStartTime = System.currentTimeMillis()
        }

        if (webHookUrl.isEmpty())
            assets["large_image"] =  APP_ASSET_LARGE_ICON
        else
            assets["large_image"] = if (status == StatusHelper.Status.JoinMe || status == StatusHelper.Status.Active) { mp.convertImageUrl(worldUrl) } else { APP_ASSET_LARGE_ICON }

        assets["large_text"] = "Powered by VRCAA"

        assets["small_image"] = when(status) {
            StatusHelper.Status.JoinMe -> APP_ASSET_SMALL_ICON_JOIN_ME
            StatusHelper.Status.Active -> APP_ASSET_SMALL_ICON_ACTIVE
            StatusHelper.Status.AskMe -> APP_ASSET_SMALL_ICON_ASK_ME
            StatusHelper.Status.Busy -> APP_ASSET_SMALL_ICON_BUSY
            StatusHelper.Status.Offline -> { "" }
        }

        assets["small_text"] = status.toString()

        val timestamps = ArrayMap<String, Any>()
        timestamps["start"] = sessionStartTime

        val activity = ArrayMap<String, Any>()
        activity["name"] = "VRChat"
        activity["application_id"] = APP_ID
        activity["state"] = if (status == StatusHelper.Status.JoinMe || status == StatusHelper.Status.Active) { worldName } else { "User location hidden." }
        activity["details"] = if (status == StatusHelper.Status.JoinMe || status == StatusHelper.Status.Active) { worldInfo } else { status.toString() }
        activity["type"] = 0
        activity["timestamps"] = timestamps
        activity["assets"] = assets

        val presence = ArrayMap<String, Any?>()
        presence["status"] = "idle"
        presence["since"] = 0
        presence["activities"] = arrayOf<Any>(activity)
        presence["afk"] = false

        val presencePayload = ArrayMap<String, Any>()
        presencePayload["op"] = 3
        presencePayload["d"] = presence

        socket?.send(gson.toJson(presencePayload))
    }

    private fun sendIdentity() {

        val deviceProperties = ArrayMap<String, Any>()

        deviceProperties["os"] = "Mac OS X"
        deviceProperties["browser"] = "Discord Client"
        deviceProperties["release_channel"] = "stable"
        deviceProperties["client_version"] = "0.0.343"
        deviceProperties["osVersion"] = "24.0.0"
        deviceProperties["osArch"] = "arm64"
        deviceProperties["appArch"] = "arm64"
        deviceProperties["systemLocale"] = "en-US"
        deviceProperties["has_client_mods"] = false
        deviceProperties["browser_user_agent"] = USER_AGENT
        deviceProperties["browser_version"] = "33.4.0"
        deviceProperties["os_sdk_version"] = "24"
        deviceProperties["client_build_number"] = 285561
        deviceProperties["native_build_number"] = null
        deviceProperties["client_event_source"] = null

        val presence = ArrayMap<String, Any>()
        presence["status"] = "unknown"
        presence["since"] = 0
        presence["activities"] = arrayOf<Any>()
        presence["afk"] = false

        val clientState = ArrayMap<String, Any>()
        clientState["guild_versions"] =  ArrayMap<String, Any>()

        val data = ArrayMap<String, Any>()
        data["token"] = token
        data["capabilities"] = 161789
        data["properties"] = deviceProperties
        data["presence"] = presence
        data["compress"] = false
        data["client_state"] = clientState

        val identityPayload = ArrayMap<String, Any>()
        identityPayload["op"] = 2
        identityPayload["d"] = data

        socket?.send(gson.toJson(identityPayload))
    }

    private fun sendResume() {

        val data = ArrayMap<String, Any>()
        data["token"] = token
        data["session_id"] = sessionId
        data["seq"] = sequence

        val resumePayload = ArrayMap<String, Any>()
        resumePayload["op"] = 6
        resumePayload["d"] = data

        socket?.send(gson.toJson(resumePayload))

        shouldResume = false
    }

    private fun sendHeartbeat() {

        val heartbeatPayload = ArrayMap<String, Any>()

        heartbeatPayload["op"] = 1
        heartbeatPayload["d"] = if (sequence == 0) null else sequence

        socket?.send(gson.toJson(heartbeatPayload))
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) discord/0.0.343 Chrome/130.0.6723.191 Electron/33.4.0 Safari/537.36"

        private const val APP_ID = "1219592758914977913"
        private const val APP_ASSET_LARGE_ICON = "1219760377718636665"
        private const val APP_ASSET_SMALL_ICON_JOIN_ME = "1230448141304856577"
        private const val APP_ASSET_SMALL_ICON_ACTIVE = "1230448141099208735"
        private const val APP_ASSET_SMALL_ICON_ASK_ME = "1230448140818317405"
        private const val APP_ASSET_SMALL_ICON_BUSY = "1230448141203931136"

        enum class Opcodes(val op: Int) {
            UNKNOWN(-1),
            DISPATCH(0),
            HELLO(10),
            HEARTBEAT_ACK(11);

            companion object {
                fun toOp(value: Int): Opcodes {
                    return entries.find { it.op == value } ?: UNKNOWN
                }
            }
        }
    }
}