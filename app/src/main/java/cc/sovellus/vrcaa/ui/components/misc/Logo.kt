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

package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R

@Composable
fun Logo(size: Dp) {
    Box(
        modifier = Modifier
            .height(size)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = if (App.isAppInDarkTheme()) {
                painterResource(R.drawable.logo_dark)
            } else {
                painterResource(R.drawable.logo_white)
            },
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.Center
        )
    }
}