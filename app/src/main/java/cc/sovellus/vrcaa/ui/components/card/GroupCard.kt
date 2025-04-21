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

package cc.sovellus.vrcaa.ui.components.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.misc.Languages
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GroupCard(
    groupName: String,
    shortCode: String,
    discriminator: String,
    bannerUrl: String,
    iconUrl: String,
    totalMembers: Int,
    languages: List<String>? = null,
    callback: (() -> Unit?)? = null
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(280.dp)
            .padding(16.dp)
            .fillMaxHeight()
            .widthIn(Dp.Unspecified, 520.dp)
            .fillMaxWidth()
            .clickable(onClick = {
                if (callback != null) {
                    callback()
                }
            })
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy((-50).dp),
            modifier = Modifier.height(220.dp).fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier.height(180.dp)
                ) {
                    GlideImage(
                        model = bannerUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop,
                        loading = placeholder(R.drawable.image_placeholder),
                        failure = placeholder(R.drawable.image_placeholder)
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest, modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        GlideImage(
                            model = iconUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            loading = placeholder(R.drawable.icon_placeholder),
                            failure = placeholder(R.drawable.icon_placeholder)
                        )
                    }
                }
            }

            item {
                Row {
                    Text(
                        text = groupName,
                        modifier = Modifier.padding(start = 104.dp).weight(0.70f),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(0.30f).padding(end = 8.dp), horizontalArrangement = Arrangement.End
                    ) {
                        languages?.let {
                            Languages(languages = it, true)
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "$shortCode.$discriminator",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(0.80f),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$totalMembers",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(0.20f),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            )
            Icon(
                modifier = Modifier.padding(end = 16.dp),
                imageVector = Icons.Filled.Group,
                contentDescription = null
            )
        }
    }
}