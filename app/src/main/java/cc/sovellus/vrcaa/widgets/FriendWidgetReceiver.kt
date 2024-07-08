package cc.sovellus.vrcaa.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class FriendWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FriendWidget()
    private val coroutineScope = MainScope()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "FRIEND_LOCATION_UPDATE") {
            this.update(context)
        }
        super.onReceive(context, intent)
    }

    private fun update(context: Context) {
        coroutineScope.launch {
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(FriendWidget::class.java)
            glanceIds.forEach { glance ->
                glanceAppWidget.update(context, glance)
            }
        }
    }
}