package cc.sovellus.vrcaa.updater

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.updater.models.Commits
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import ru.gildor.coroutines.okhttp.await
import java.io.File


object AutoUpdater {

    private val client: OkHttpClient by lazy { OkHttpClient() }

    private const val author = "Nyabsi"
    private const val repo = "vrcaa"

    private const val updateUrl = "https://github.com/$author/$repo/releases/download/latest/VRCAA-signed.apk"
    private const val updateUrlQuest = "https://github.com/$author/$repo/releases/download/latest/VRCAA-quest-signed.apk"

    private suspend fun getLatestCommitHash(): String {
        val request = Request.Builder()
            .url("https://api.github.com/repos/$author/$repo/commits")
            .get()
            .build()

        val response = client.newCall(request).await()
        val data = Gson().fromJson(response.body?.string()!!, Commits::class.java)
        return data[0].sha.substring(0, 7)
    }

    suspend fun checkForUpdates(): Boolean {
        return getLatestCommitHash() != BuildConfig.GIT_HASH
    }

    suspend fun downloadUpdate(destination: File): Boolean {

        val url: String = if (BuildConfig.FLAVOR == "standard") { updateUrl } else { updateUrlQuest }

        val request: Request = Request.Builder().url(url).build()

        val response = client.newCall(request).await()

        if (!response.isSuccessful)
            return false

        val sink: BufferedSink = destination.sink().buffer()
        sink.writeAll(response.body!!.source())
        sink.close()

        return true
    }

    fun installUpdate(context: Context, update: File) {
        if (update.exists()) {
            val uri = FileProvider.getUriForFile(context, context.packageName, update)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }
    }
}