package cc.sovellus.vrcaa.ui.components.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FriendItem(friend: Friend, callback: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(friend.displayName)
        },
        overlineContent = {
            Text(friend.statusDescription.ifEmpty {
                StatusHelper.getStatusFromString(friend.status).toString()
            }, maxLines = 1)
        },
        supportingContent = {
            Text(text = (if (friend.platform == "web") { "Active on website." } else { LocationHelper.getReadableLocation(friend.location) }), maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        leadingContent = {
            GlideImage(
                model = friend.userIcon.ifEmpty { friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl } },
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder)
            )
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.size(20.dp)
            ) {
                Badge(
                    containerColor = StatusHelper.getStatusFromString(friend.status).toColor(), modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        },
        modifier = Modifier.clickable(
            onClick = {
                callback()
            }
        )
    )
}