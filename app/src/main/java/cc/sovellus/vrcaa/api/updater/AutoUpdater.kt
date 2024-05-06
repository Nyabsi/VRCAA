package cc.sovellus.vrcaa.api.updater

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.updater.models.GitRef
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import ru.gildor.coroutines.okhttp.await
import java.io.File


class AutoUpdater(
    private val context: Context
) {

    private val client: OkHttpClient by lazy { OkHttpClient() }
    private val path = File(context.filesDir, "temp.apk")

    private suspend fun getLatestCommitHash(): String {
        val request = Request.Builder()
            .url("https://api.github.com/repos/$author/$repo/git/ref/tags/latest")
            .get()
            .build()

        val response = client.newCall(request).await()
        val data = Gson().fromJson(response.body?.string()!!, GitRef::class.java)
        return data.objectX.sha.substring(0, 7)
    }

    suspend fun checkForUpdates(): Boolean {
        return getLatestCommitHash() != BuildConfig.GIT_HASH
    }

    suspend fun downloadUpdate(): Boolean {

        val url: String = if (BuildConfig.FLAVOR == "standard") { updateUrl } else { updateUrlQuest }

        val request: Request = Request.Builder().url(url).build()

        val response = client.newCall(request).await()

        if (!response.isSuccessful)
            return false

        val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, cc.sovellus.vrcaa.manager.NotificationManager.CHANNEL_UPDATE_ID)
            .setOngoing(true)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.update_notification_title_ongoing))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setSilent(true)

        val totalBytes = response.body?.contentLength() ?: -1
        var bytesDownloaded: Long = 0
        var totalProgress = 0

        val sink: BufferedSink = path.sink().buffer()
        val source = response.body?.source()

        source?.let { src ->
            while (true) {
                val bytesRead = src.read(sink.buffer, 8192 * 4)
                if (bytesRead == -1L) break

                bytesDownloaded += bytesRead
                sink.emitCompleteSegments()

                val progress = ((bytesDownloaded.toFloat() / totalBytes.toFloat()) * 100).toInt()

                if ((progress % 10) == 0 && totalProgress != progress) {
                    builder.setProgress(100, progress, false)
                    notifyManager.notify(notificationId, builder.build())
                    totalProgress = progress
                }
            }
        }

        sink.close()

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
        notifyManager.notify(notificationId, notification)

        return true
    }

    companion object {
        private const val author = "Nyabsi"
        private const val repo = "vrcaa"

        private const val updateUrl = "https://github.com/$author/$repo/releases/download/latest/VRCAA-signed.apk"
        private const val updateUrlQuest = "https://github.com/$author/$repo/releases/download/latest/VRCAA-quest-signed.apk"

        const val notificationId = 1234
    }
}