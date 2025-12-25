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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun InstanceItem(instance: Instance, creator: String?, friends: List<Friend>, onClick: () -> Unit) {
    val result = LocationHelper.parseLocationInfo(instance.instanceId)
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .padding(4.dp)
    ) {
        ListItem(
            headlineContent = {

            },
            overlineContent = {
                val label = buildAnnotatedString {
                    append(creator ?: instance.world.authorName)
                    append(" ")
                    append("#${instance.name}")
                    append(" ")
                    append(result.instanceType)
                    if (result.ageGated) {
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append("AGE GATED")
                        }
                    }
                }
                Text(label)
            },
            supportingContent = {
                Column {
                    Spacer(Modifier.padding(4.dp))
                    Row {
                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = "${instance.userCount} / ${instance.world.capacity}",
                                    modifier = Modifier.padding(end = 2.dp)
                                )
                                Icon(
                                    imageVector = Icons.Filled.Group,
                                    contentDescription = null
                                )
                            }
                        }
                        Spacer(Modifier.padding(start = 4.dp, end = 4.dp))
                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = friends.size.toString(),
                                    modifier = Modifier.padding(end = 2.dp)
                                )
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    if (friends.isNotEmpty()) {
                        VerticalDivider(Modifier.padding(4.dp))

                        friends.forEach { friend ->
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Badge(
                                        containerColor = StatusHelper.getStatusFromString(friend.status)
                                            .toColor(), modifier = Modifier.size(40.dp)
                                    ) {
                                        GlideImage(
                                            model = friend.userIcon.ifEmpty { friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl } },
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(50)),
                                            contentScale = ContentScale.Crop,
                                            loading = placeholder(R.drawable.image_placeholder),
                                            failure = placeholder(R.drawable.image_placeholder),
                                            alpha = 0.8f
                                        )
                                    }
                                    Spacer(Modifier.padding(start = 4.dp, end = 4.dp))
                                    Text(
                                        text = friend.displayName,
                                        maxLines = 1,
                                        fontWeight = FontWeight.Normal,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            trailingContent = {
                if (result.regionId.isNotEmpty()) {
                    when (result.regionId.lowercase()) {
                        "eu" -> Image(
                            painter = painterResource(R.drawable.flag_eu),
                            modifier = Modifier.padding(start = 2.dp),
                            contentDescription = "Region flag"
                        )
                        "jp" -> Image(
                            painter = painterResource(R.drawable.flag_jp),
                            modifier = Modifier.padding(start = 2.dp),
                            contentDescription = "Region flag"
                        )
                        "us" -> Image(
                            painter = painterResource(R.drawable.flag_us),
                            modifier = Modifier.padding(start = 2.dp),
                            contentDescription = "Region flag"
                        )
                        "use" -> Image(
                            painter = painterResource(R.drawable.flag_us),
                            modifier = Modifier.padding(start = 2.dp),
                            contentDescription = "Region flag"
                        )
                        "usw" -> Image(
                            painter = painterResource(R.drawable.flag_us),
                            modifier = Modifier.padding(start = 2.dp),
                            contentDescription = "Region flag"
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(R.drawable.flag_us),
                        modifier = Modifier.padding(start = 2.dp),
                        contentDescription = "Region flag",
                    )
                }
            },
            modifier = Modifier.clickable(
                onClick = {
                    onClick()
                }
            )
        )
    }
}