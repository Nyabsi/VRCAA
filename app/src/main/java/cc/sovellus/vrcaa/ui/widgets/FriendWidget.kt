package cc.sovellus.vrcaa.ui.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FriendWidget : GlanceAppWidget() {
    private var friendCount: Int = 0

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        withContext(Dispatchers.IO) {
           // api.let { friendCount = it.getFriends()?.size ?: 0 }
        }

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(imageProvider = ImageProvider(R.drawable.widget_background)),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You have $friendCount friends online.",
                modifier = GlanceModifier.padding(12.dp)
            )

        }
    }
}