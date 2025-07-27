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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Badge
import cc.sovellus.vrcaa.ui.components.misc.Languages
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileCard(
    thumbnailUrl: String,
    iconUrl: String,
    displayName: String,
    statusDescription: String,
    trustRankColor: Color,
    statusColor: Color,
    tags: List<String>,
    badges: List<Badge>,
    pronouns: String,
    ageVerificationStatus: String,
    disablePeek: Boolean = true,
    onPeek: (url: String) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(270.dp)
            .widthIn(Dp.Unspecified, 520.dp)
            .fillMaxWidth()
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy((-50).dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                GlideImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable(
                            enabled = !disablePeek,
                            onClick = {
                            onPeek(thumbnailUrl)
                        }),
                    contentScale = ContentScale.Crop,
                    loading = placeholder(R.drawable.image_placeholder),
                    failure = placeholder(R.drawable.image_placeholder)
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable(
                                enabled = !disablePeek,
                                onClick = {
                                onPeek(iconUrl)
                            })
                    ) {
                        GlideImage(
                            model = iconUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            loading = placeholder(R.drawable.image_placeholder),
                            failure = placeholder(R.drawable.image_placeholder)
                        )
                    }
                }
            }

            item {
                Row {
                    Row(
                        modifier = Modifier.padding(start = 12.dp, top = 60.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = displayName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Left,
                            color = trustRankColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (ageVerificationStatus == "18+") {
                            Badge(
                                containerColor = Color(0xFF606FE4),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    modifier = Modifier.padding(2.dp),
                                    text = "18+",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Left,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.White
                                )
                            }
                        }
                        if (pronouns.isNotEmpty()) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    modifier = Modifier.padding(2.dp),
                                    text = pronouns,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Left,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Languages(languages = tags, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(start = 12.dp, top = 50.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Badge(containerColor = statusColor, modifier = Modifier.size(16.dp))
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = statusDescription,
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.SemiBold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        for (badge in badges) {
                            GlideImage(model = badge.badgeImageUrl, contentDescription = null, modifier = Modifier
                                .size(28.dp)
                                .padding(2.dp), alpha = if (badge.showcased) { 1.0f } else { 0.5f })
                        }
                    }
                }
            }
        }
    }
}