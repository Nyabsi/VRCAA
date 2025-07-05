/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.components.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import cc.sovellus.vrcaa.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FeedItem(
    text: AnnotatedString,
    friendPictureUrl: String,
    feedTimestamp: LocalDateTime,
    resourceStringTitle: Int,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(text, maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        leadingContent = {
            Column {
                Badge(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    GlideImage(
                        model = friendPictureUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(50)),
                        contentScale = ContentScale.FillBounds,
                        alignment = Alignment.Center,
                        loading = placeholder(R.drawable.image_placeholder),
                        failure = placeholder(R.drawable.image_placeholder)
                    )
                }
            }
        },
        overlineContent = {
            Text(stringResource(resourceStringTitle))
        },
        trailingContent = {
            val userTimeZone = TimeZone.getDefault().toZoneId()
            val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
            Text(text = feedTimestamp.atZone(userTimeZone).format(formatter))
        },
        modifier = Modifier.clickable(
            onClick = { onClick() }
        )
    )
}