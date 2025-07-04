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

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
fun DisclaimerDialog(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    title: AnnotatedString,
    description: AnnotatedString,
) {
    val context = LocalContext.current
    val consentOfWithdrawal = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Column {
                Text(text = description)
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(modifier = Modifier.weight(0.80f), text = "I understand, when I click \"Continue\" I waive my rights for complaints.")
                    Switch(
                        modifier = Modifier.weight(0.20f),
                        checked = consentOfWithdrawal.value,
                        onCheckedChange = { toggle -> consentOfWithdrawal.value = toggle },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        )
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
                    onDismiss()
                }
            ) {
                Text("Go Back")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (!consentOfWithdrawal.value) {
                        Toast.makeText(
                            context,
                            "Please toggle the checkbox first!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        onConfirmation()
                    }
                }
            ) {
                Text("Continue")
            }
        }
    )
}