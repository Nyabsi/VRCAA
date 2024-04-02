package cc.sovellus.vrcaa.ui.components.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun UpdatedDialog(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    title: String,
    description: String,
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = description)
        },
        icon = {
          Icon(imageVector = Icons.Filled.Info, contentDescription = "Update")
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
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("No")
            }
        }
    )
}