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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.StatusHelper
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RoundedRowItem(
    name: String,
    url: Any,
    status: String,
    onClick: () -> Unit
) {
    val window = LocalWindowInfo.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(
            onClick = { onClick() }
        ).sizeIn(80.dp, 80.dp, 100.dp, 100.dp).aspectRatio(1f / 1f)
    ) {
        Badge(
            containerColor = StatusHelper.getStatusFromString(status).toColor(), modifier = Modifier.size(72.dp).align(Alignment.CenterHorizontally)
        ) {
            GlideImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder)
            )
        }
        Text(text = name, softWrap = true, overflow = TextOverflow.Ellipsis)
    }
}