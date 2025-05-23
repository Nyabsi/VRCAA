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

package cc.sovellus.vrcaa.ui.components.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cc.sovellus.vrcaa.R

@Composable
fun NoInternetDialog(
    onClick: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.misc_no_internet_title))
        },
        text = {
            Text(text = stringResource(R.string.misc_no_internet_description))
        },
        icon = {
            Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onClick()
                }
            ) {
                Text(stringResource(R.string.misc_no_internet_label))
            }
        }
    )
}