package cc.sovellus.vrcaa.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun InputDialog(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    title: String,
    text: MutableState<String>
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = text.value,
                    onValueChange = { text.value = it },
                    label = { Text(text = title) },
                    singleLine = true,
                )
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Discard")
            }
        }
    )
}