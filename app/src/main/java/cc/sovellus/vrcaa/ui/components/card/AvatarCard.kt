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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AvatarCard(avatar: Avatar) {

    var foundWindows by remember { mutableStateOf(false) }
    var foundAndroid by remember { mutableStateOf(false) }
    var foundDarwin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        avatar.unityPackages.forEach { pkg ->
            when (pkg.platform) {
                "android" -> {
                    foundAndroid = true

                }
                "standalonewindows" -> {
                    foundWindows = true
                }
                "ios" -> {
                    foundDarwin = true
                }
            }
        }
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(240.dp)
            .widthIn(0.dp, 520.dp)
            .fillMaxWidth()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.TopStart
        ) {
            GlideImage(
                model = avatar.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .zIndex(0f),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                    .zIndex(1f),
                horizontalArrangement = Arrangement.End
            ) {
                if (foundWindows) {
                    Badge(
                        containerColor = Color(0, 168, 252, 191),
                        modifier = Modifier
                            .height(height = 26.dp)
                            .padding(start = 2.dp, top = 8.dp),
                        content = {
                            Text(
                                text = "Windows"
                            )
                        }
                    )
                }

                if (foundAndroid) {
                    Badge(
                        containerColor = Color(103, 215, 129, 191),
                        modifier = Modifier
                            .height(height = 26.dp)
                            .padding(start = 2.dp, top = 8.dp),
                        content = {
                            Text(
                                text = "Android"
                            )
                        }
                    )
                }

                if (foundDarwin) {
                    Badge(
                        containerColor = Color(121, 136, 151, 191),
                        modifier = Modifier
                            .height(height = 26.dp)
                            .padding(start = 2.dp, top = 8.dp),
                        content = {
                            Text(
                                text = "iOS"
                            )
                        }
                    )
                }
            }
        }

        Text(
            text = avatar.name,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(R.string.avatar_text_author).format(avatar.authorName),
            modifier = Modifier.padding(start = 12.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}