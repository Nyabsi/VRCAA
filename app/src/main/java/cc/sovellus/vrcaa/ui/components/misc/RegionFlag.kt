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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R

@Composable
fun RegionFlag(regionId: String) {
    if (regionId.isNotEmpty()) {
        when (regionId.lowercase()) {
            "eu" -> Image(
                painter = painterResource(R.drawable.flag_eu),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )

            "jp" -> Image(
                painter = painterResource(R.drawable.flag_jp),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )

            "us" -> Image(
                painter = painterResource(R.drawable.flag_us),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )

            "use" -> Image(
                painter = painterResource(R.drawable.flag_us),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription =null
            )

            "usw" -> Image(
                painter = painterResource(R.drawable.flag_us),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )
        }
    } else {
        Image(
            painter = painterResource(R.drawable.flag_us),
            modifier = Modifier.padding(start = 2.dp),
            contentDescription = null
        )
    }
}