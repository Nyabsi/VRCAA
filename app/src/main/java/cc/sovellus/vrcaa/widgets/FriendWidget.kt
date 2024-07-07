package cc.sovellus.vrcaa.widgets

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.manager.FriendManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FriendWidget : GlanceAppWidget() {

    private lateinit var friends: MutableList<Friend>
    private var images: MutableMap<String, Bitmap> = mutableMapOf()

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        friends = FriendManager.getFriends().filter {
            it.location.contains("wrld_")
        }.toMutableList()

        withContext(Dispatchers.IO) {
            context.loadImages()
        }

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        Box(
            modifier = GlanceModifier.fillMaxSize()
                .background(imageProvider = ImageProvider(R.drawable.widget_background))
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Horizontal.Start,
                modifier = GlanceModifier
                    .padding(4.dp),
            ) {
                Text(
                    text = "Active friends: ${friends.count()}",
                    style = TextStyle(textAlign = TextAlign.Start, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }
            LazyColumn(
                horizontalAlignment = Alignment.Horizontal.Start,
                modifier = GlanceModifier
                    .padding(top =  24.dp),
            ) {
                items(friends) { friend ->
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                            modifier = GlanceModifier.fillMaxWidth()
                                .background(imageProvider = ImageProvider(R.drawable.widget_background_accent))
                                .padding(8.dp)
                                .cornerRadius(10.dp)
                        ) {
                            val bitmap = images[friend.id]
                            Image(ImageProvider(bitmap!!), contentDescription = null, modifier = GlanceModifier.size(48.dp).cornerRadius(50.dp))
                            Column {
                                Text(text = "${friend.displayName} is current at", modifier = GlanceModifier.padding(horizontal = 4.dp), maxLines = 1)
                                Text(text = LocationHelper.getReadableLocation(friend.location), modifier = GlanceModifier.padding(horizontal = 4.dp), maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Context.loadImages() {
        friends.forEach {
            val bitmap = Glide.with(this)
                .asBitmap()
                .load(it.userIcon.ifEmpty { it.currentAvatarImageUrl })
                .circleCrop()
                .submit(128, 128)
                .get()
            images[it.id] = bitmap
        }
    }
}