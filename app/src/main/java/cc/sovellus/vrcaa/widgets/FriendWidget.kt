package cc.sovellus.vrcaa.widgets

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
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

    private var friends: MutableList<Friend> = mutableListOf()
    private var images: MutableMap<String, Bitmap> = mutableMapOf()

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(MINIFIED_LIST, EXTENDED_LIST)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        withContext(Dispatchers.IO) {
            friends = FriendManager.getFriends().filter {
                it.location.contains("wrld_")
            }.toMutableList()
            context.loadImages()
        }

        provideContent {
            GlanceTheme {
                when (LocalSize.current) {
                    MINIFIED_LIST -> WidgetFriendList(context, showIcons = false)
                    EXTENDED_LIST -> WidgetFriendList(context, showIcons = true)
                }
            }
        }
    }

    @Composable
    private fun WidgetFriendList(context: Context, showIcons: Boolean) {
        if (friends.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize()
                    .background(imageProvider = ImageProvider(R.drawable.widget_background)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = context.applicationContext.getString(R.string.widget_name_loading_text))
            }
        } else {
            Box(
                modifier = GlanceModifier.fillMaxSize()
                    .background(imageProvider = ImageProvider(R.drawable.widget_background))
                    .padding(16.dp),
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.Horizontal.Start
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
                                    .padding(4.dp)
                                    .cornerRadius(10.dp)
                            ) {
                                if (showIcons) {
                                    val bitmap = images[friend.id]
                                    Image(ImageProvider(bitmap!!), contentDescription = null, modifier = GlanceModifier.size(32.dp).cornerRadius(50.dp))
                                }
                                Column {
                                    Text(text = friend.displayName, modifier = GlanceModifier.padding(horizontal = 4.dp), maxLines = 1)
                                    Text(text = LocationHelper.getReadableLocation(friend.location), modifier = GlanceModifier.padding(horizontal = 4.dp), maxLines = 1)
                                }
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

    companion object {
        private val MINIFIED_LIST = DpSize(110.dp, 0.dp)
        private val EXTENDED_LIST = DpSize(180.dp, 0.dp)
    }
}