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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R

@Composable
fun PrintUploadConfigDialog(
    onDismiss: () -> Unit,
    onConfirmation: (note: String, border: Boolean) -> Unit
) {
    val note = remember { mutableStateOf("") }
    val enableBorder = remember { mutableStateOf(false) }

    AlertDialog(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),

        title = {
            Text(text = "Print Upload Config")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = note.value,
                    onValueChange = { note.value = it },
                    label = { Text(text = "Note") },
                    singleLine = true,
                )

                Spacer(Modifier.padding(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = {
                            enableBorder.value = !enableBorder.value
                        }
                    )
                ) {
                    Text("Enable Border")

                    Switch(
                        checked = enableBorder.value,
                        onCheckedChange = {
                            enableBorder.value = it
                        }
                    )
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(note.value, enableBorder.value)
                }
            ) {
                Text(stringResource(R.string.generic_text_upload))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.generic_text_cancel))
            }
        }
    )
}