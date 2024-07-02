package cc.sovellus.vrcaa

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (it.action) {
                "CUSTOM_ACTION_UPDATE_CANCEL" -> {
                    context?.let { ctx ->
                        val path = File(ctx.filesDir, "temp.apk")
                        path.delete()
                    }
                }
                "CUSTOM_ACTION_UPDATE_INSTALL" -> {
                    context?.let { ctx ->
                        val path = File(ctx.filesDir, "temp.apk")
                        val uri =  FileProvider.getUriForFile(ctx, ctx.packageName, path)
                        val updateIntent = Intent(Intent.ACTION_VIEW)
                        updateIntent.setDataAndType(uri, "application/vnd.android.package-archive")
                        updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        updateIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        ctx.startActivity(updateIntent)
                    }
                }

                else -> { /* Unknown */ }
            }

            context?.let { ctx ->
                val notifyManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notifyManager.cancel(AutoUpdater.NOTIFICATION_ID)
            }
        }
    }
}