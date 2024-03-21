package cc.sovellus.vrcaa.updater

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import cc.sovellus.vrcaa.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import org.kohsuke.github.GitHub
import ru.gildor.coroutines.okhttp.await
import java.io.File


class AutoUpdater : CoroutineScope {

    override val coroutineContext = Dispatchers.IO + SupervisorJob()

    private val client: OkHttpClient by lazy { OkHttpClient() }

    private val repoOwner = "Nyabsi"
    private val repoName = "vrcaa"

    private val updateUrl = "https://github.com/$repoOwner/$repoName/releases/download/latest/VRCAA-signed.apk"
    private val updateUrlQuest = "https://github.com/$repoOwner/$repoName/releases/download/latest/VRCAA-quest-signed.apk"

    private var hasUpdate = false

    private fun getLatestCommitHash(): String {
        val github = GitHub.connectAnonymously()
        val repository = github.getRepository("$repoOwner/$repoName")
        val commit = repository.listCommits().toArray()[0]
        return commit.shA1.substring(0, 7)
    }

    fun checkForUpdates(): Boolean {
        launch {
            hasUpdate = getLatestCommitHash() != BuildConfig.GIT_HASH
        }
        return hasUpdate
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