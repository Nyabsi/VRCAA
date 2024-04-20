package cc.sovellus.vrcaa.ui.components.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FeedItem(
    text: AnnotatedString,
    friendPictureUrl: String,
    feedTimestamp: LocalDateTime,
    resourceStringTitle: Int,
    userId: String
) {
    val navigator = LocalNavigator.currentOrThrow

    ListItem(
        headlineContent = {
            Text(text, maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        leadingContent = {
            GlideImage(
                model = friendPictureUrl,
                contentDescription = stringResource(R.string.preview_image_description),
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center,
                loading = placeholder(R.drawable.icon_placeholder),
                failure = placeholder(R.drawable.icon_placeholder)
            )
        },
        overlineContent = {
            Text(stringResource(resourceStringTitle))
        },
        trailingContent = {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            Text(text = feedTimestamp.format(formatter))
        },
        modifier = Modifier.clickable(
            onClick = { navigator.parent?.parent?.push(UserProfileScreen(userId)) }
        )
    )
}