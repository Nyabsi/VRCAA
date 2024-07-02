package cc.sovellus.vrcaa

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import cc.sovellus.vrcaa.api.BaseClient
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File


class AutoUpdater(
    private val context: Context
): BaseClient() {

    private val path = File(context.filesDir, "temp.apk")

    private suspend fun getLatestCommit(): String?
    {
        val headers = Headers.Builder()
        headers["User-Agent"] = "VRCAA/1.0.0 nyaa@sovellus.cc"

        val result = doRequest(
            method = "GET",
            url = "https://api.github.com/repos/$AUTHOR_NAME/$REPOSITORY_NAME/git/ref/tags/latest",
            headers = headers,
            body = null
        )

        if (result is Result.Succeeded) {
            return Gson().fromJson(result.body, GitTag::class.java).objectX.sha.substring(0, 7)
        }

        return null
    }

    suspend fun checkForUpdates(): Boolean
    {
        val commit = getLatestCommit() ?: return false
        return commit != BuildConfig.GIT_HASH
    }

    suspend fun downloadUpdate(): Boolean
    {
        val headers = Headers.Builder()
        headers["User-Agent"] = "VRCAA/1.0.0 nyaa@sovellus.cc"

        val result = doRequest(
            method = "GET",
            url = if (BuildConfig.FLAVOR == "standard") { UPDATE_URL } else { UPDATE_URL_QUEST },
            headers = headers,
            body = null
        )

        if (result is Result.Succeeded) {
            val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val builder = NotificationCompat.Builder(context, cc.sovellus.vrcaa.manager.NotificationManager.CHANNEL_UPDATE_ID)
                .setOngoing(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.update_notification_title_ongoing))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setSilent(true)

            val totalBytes = result.response.body?.contentLength() ?: -1
            var bytesDownloaded: Long = 0
            var totalProgress = 0

            val sink: BufferedSink = path.sink().buffer()
            val source = result.response.body?.source()

            source?.let { src ->
                while (true) {
                    val bytesRead = src.read(sink.buffer, 8192 * 4)
                    if (bytesRead == -1L) break

                    bytesDownloaded += bytesRead
                    sink.emitCompleteSegments()

                    val progress = ((bytesDownloaded.toFloat() / totalBytes.toFloat()) * 100).toInt()

                    if ((progress % 10) == 0 && totalProgress != progress) {
                        builder.setProgress(100, progress, false)
                        notifyManager.notify(NOTIFICATION_ID, builder.build())
                        totalProgress = progress
                    }
                }
            }

            withContext(Dispatchers.IO) { sink.close() }

            val cancelIntent = Intent(context, UpdateReceiver::class.java)
                .setAction("CUSTOM_ACTION_UPDATE_CANCEL")

            val cancelIntentPending = PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

            val installIntent = Intent(context, UpdateReceiver::class.java)
                .setAction("CUSTOM_ACTION_UPDATE_INSTALL")

            val installIntentPending = PendingIntent.getBroadcast(context, 1, installIntent, PendingIntent.FLAG_IMMUTABLE)

            builder
                .setContentText(context.getString(R.string.update_notification_title_done))
                .setProgress(0, 0, false)
                .addAction(0, context.getString(R.string.update_notification_action_cancel), cancelIntentPending)
                .addAction(0, context.getString(R.string.update_notification_action_install), installIntentPending)

            val notification = builder.build()

            notification.flags = Notification.FLAG_ONGOING_EVENT
            notifyManager.notify(NOTIFICATION_ID, notification)

            return true
        }

        return false
    }

    data class GitTag(
        @SerializedName("node_id")
        val nodeId: String,
        @SerializedName("object")
        val objectX: Object,
        @SerializedName("ref")
        val ref: String,
        @SerializedName("url")
        val url: String
    ) {
        data class Object(
            @SerializedName("sha")
            val sha: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("url")
            val url: String
        )
    }

    companion object {
        private const val AUTHOR_NAME = "nyabsi"
        private const val REPOSITORY_NAME = "vrcaa"

        private const val UPDATE_URL = "https://github.com/$AUTHOR_NAME/$REPOSITORY_NAME/releases/download/latest/VRCAA-signed.apk"
        private const val UPDATE_URL_QUEST = "https://github.com/$AUTHOR_NAME/$REPOSITORY_NAME/releases/download/latest/VRCAA-quest-signed.apk"

        const val NOTIFICATION_ID = 1234
    }
}