package cc.sovellus.vrcaa.ui.screen.feed.components

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
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FeedItem(
    text: AnnotatedString,
    friendPictureUrl: String,
    feedTimestamp: LocalDateTime,
    resourceStringTitle: Int
) {
    ListItem(
        headlineContent = {
            Text(text)
        },
        leadingContent = {
            GlideImage(
                model = friendPictureUrl,
                contentDescription = stringResource(R.string.preview_image_description),
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center
            )
        },
        overlineContent = {
            Text(stringResource(resourceStringTitle))
        },
        trailingContent = {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            Text(text = feedTimestamp.format(formatter))
        }
    )
}