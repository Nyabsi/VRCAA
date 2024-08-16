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