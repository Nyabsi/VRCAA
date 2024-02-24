package cc.sovellus.vrcaa.api

import android.util.Log
import cc.sovellus.vrcaa.api.models.pipeline.FriendActive
import cc.sovellus.vrcaa.api.models.pipeline.FriendAdd
import cc.sovellus.vrcaa.api.models.pipeline.FriendDelete
import cc.sovellus.vrcaa.api.models.pipeline.FriendLocation
import cc.sovellus.vrcaa.api.models.pipeline.FriendOffline
import cc.sovellus.vrcaa.api.models.pipeline.FriendOnline
import cc.sovellus.vrcaa.api.models.pipeline.Notification
import cc.sovellus.vrcaa.api.models.pipeline.NotificationV2
import cc.sovellus.vrcaa.api.models.pipeline.UpdateModel
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class PipelineContext(
    token: String
) {

    private lateinit var socket: WebSocket
    private val client: OkHttpClient by lazy { OkHttpClient() }
    private val userAgent: String = "VRCAA/0.1 nyabsi@sovellus.cc"
    private var shouldReconnect: Boolean = false

    interface SocketListener {
        fun onMessage(message: Any?)
    }

    private var socketListener: SocketListener? = null

    private val listener by lazy {
        object : WebSocketListener()
        {
            override fun onOpen(
                webSocket: WebSocket, response: Response
            ) {
                Log.d("VRCAA", "Connected to the VRChat pipeline.")
            }

            override fun onMessage(
                webSocket: WebSocket, text: String)
            {
                val update = Gson().fromJson(text, UpdateModel::class.java)

                when (update.type) {
                    "friend-location" -> {
                        val location = Gson().fromJson(update.content, FriendLocation::class.java)
                        socketListener?.onMessage(location)
                    }
                    "friend-active" -> {
                        val friend = Gson().fromJson(update.content, FriendActive::class.java)
                        socketListener?.onMessage(friend)
                    }
                    "friend-online" -> {
                        val friend = Gson().fromJson(update.content, FriendOnline::class.java)
                        socketListener?.onMessage(friend)
                    }
                    "friend-offline" -> {
                        val friend = Gson().fromJson(update.content, FriendOffline::class.java)
                        socketListener?.onMessage(friend)
                    }
                    "friend-delete" -> {
                        val friend = Gson().fromJson(update.content, FriendDelete::class.java)
                        socketListener?.onMessage(friend)
                    }
                    "friend-add" -> {
                        val friend = Gson().fromJson(update.content, FriendAdd::class.java)
                        socketListener?.onMessage(friend)
                    }
                    "notification" -> {
                        val notification = Gson().fromJson(update.content, Notification::class.java)
                        socketListener?.onMessage(notification)
                    }
                    "notification-v2" -> {
                        val notification = Gson().fromJson(update.content, NotificationV2::class.java)
                        socketListener?.onMessage(notification)
                    }
                    else -> {
                        Log.d("VRCAA", "Got Unknown pipeline message (${update.type})")
                        Log.d("VRCAA", update.content)
                        socketListener?.onMessage(null)
                    }
                }
            }

            override fun onClosing(
                webSocket: WebSocket, code: Int, reason: String
            ) {
                // shouldReconnect = false
            }

            override fun onClosed(
                webSocket: WebSocket, code: Int, reason: String
            ) {
                Log.d("VRCAA", "Pipeline disconnected.")
                if (shouldReconnect) {
                    connect()
                }
            }

            override fun onFailure(
                webSocket: WebSocket, t: Throwable, response: Response?
            ) {
                when (response?.code) {
                    401 -> {
                        shouldReconnect = false // no you shouldn't reconnect in-case of "Wrong Credentials" Failure.
                    }
                    else -> {
                        Log.d("VRCAA", t.message.toString())
                    }
                }
            }
        }
    }

    private val pipelineUrl: String = "wss://pipeline.vrchat.cloud/?auth=${token}"

    fun connect() {
        val headers = Headers.Builder()
        headers["User-Agent"] = userAgent

        val request = Request.Builder()
            .url(url = pipelineUrl)
            .headers(headers = headers.build())
            .build()

        socket = client.newWebSocket(request, listener)

        // Shutdown OkHttpClient after we're sent the initial "handshake" to initialize WebSocket
        client.dispatcher.executorService.shutdown()

        shouldReconnect = true // yes, we should.
    }

    fun disconnect() {
        shouldReconnect = true
    }

    fun setListener(listener: SocketListener) {
        socketListener = listener
    }
}
