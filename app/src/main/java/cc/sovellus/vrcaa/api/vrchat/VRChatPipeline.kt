package cc.sovellus.vrcaa.api.vrchat

import android.util.Log
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendActive
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendAdd
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendDelete
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendLocation
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendOffline
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendOnline
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendUpdate
import cc.sovellus.vrcaa.api.vrchat.models.websocket.Notification
import cc.sovellus.vrcaa.api.vrchat.models.websocket.NotificationV2
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UpdateModel
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UserLocation
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class VRChatPipeline(
    token: String
) {

    private lateinit var socket: WebSocket
    private val client: OkHttpClient by lazy { OkHttpClient() }
    private val userAgent: String = "VRCAA/0.1 nyabsi@sovellus.cc"
    private var shouldReconnect: Boolean = false

    private val restartWorker: Runnable = Runnable {
        Thread.sleep(RESTART_INTERVAL)

        disconnect()
        connect()
    }

    private var restartThread: Thread? = null

    interface SocketListener {
        fun onMessage(message: Any?)
    }

    private var socketListener: SocketListener? = null

    private val listener by lazy {
        object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket, response: Response
            ) {
                Log.d("VRCAA", "Connected to the VRChat pipeline.")
                shouldReconnect = true

                restartThread = Thread(restartWorker)
                restartThread?.start()
            }

            override fun onMessage(
                webSocket: WebSocket, text: String
            ) {
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

                    "friend-update" -> {
                        val friend = Gson().fromJson(update.content, FriendUpdate::class.java)
                        socketListener?.onMessage(friend)
                    }

                    "user-location" -> {
                        val location = Gson().fromJson(update.content, UserLocation::class.java)
                        socketListener?.onMessage(location)
                    }

                    "notification" -> {
                        val notification = Gson().fromJson(update.content, Notification::class.java)
                        socketListener?.onMessage(notification)
                    }

                    "notification-v2" -> {
                        val notification =
                            Gson().fromJson(update.content, NotificationV2::class.java)
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
                if (shouldReconnect) {
                    Thread.sleep(RECONNECTION_INTERVAL) // wait 3 seconds before reconnecting.
                    connect()
                }
            }
            
            override fun onFailure(
                webSocket: WebSocket, t: Throwable, response: Response?
            ) {
                when (response?.code) {
                    401 -> {
                        shouldReconnect = false
                    }
                }

                if (shouldReconnect) {
                    Thread.sleep(RECONNECTION_INTERVAL) // wait 3 seconds before reconnecting.
                    connect()
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
    }

    fun disconnect() {
        shouldReconnect = false
        socket.close(1000, "")
    }

    fun setListener(listener: SocketListener) {
        socketListener = listener
    }

    companion object {
        private const val RESTART_INTERVAL: Long = 1800000
        private const val RECONNECTION_INTERVAL: Long = 3000
    }
}
