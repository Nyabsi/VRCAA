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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BadgesFromTags(
    tags: List<String>,
    tagPropertyName: String,
    localizationResourceInt: Int
) {
    Row(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 4.dp)
    ) {
        tags.let {
            var found = false
            for (tag in tags) {
                if (tag.contains(tagPropertyName)) {
                    found = true
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .height(height = 26.dp)
                            .padding(2.dp),
                        content = {
                            Text(
                                text = tag.substring(tagPropertyName.length + 1)
                                    .uppercase()
                                    .replace("_", " "),
                                textAlign = TextAlign.Left,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
            if (!found) {
                Text(
                    text = stringResource(localizationResourceInt),
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}