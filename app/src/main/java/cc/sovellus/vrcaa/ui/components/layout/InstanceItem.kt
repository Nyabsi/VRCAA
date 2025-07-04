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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.helper.LocationHelper

@Composable
fun InstanceItem(intent: String, instance: Instance, onClick: () -> Unit) {
    val result = LocationHelper.parseLocationInfo(intent)
    ListItem(
        headlineContent = {
            Text("Capacity: ${instance.nUsers}/${instance.world.capacity}, ${result.instanceType}")
        },
        overlineContent = {
            Text("${instance.world.name} #${instance.name}", color = if (result.ageGated) {
                Color.Red
            } else {
                Color.Unspecified
            })
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