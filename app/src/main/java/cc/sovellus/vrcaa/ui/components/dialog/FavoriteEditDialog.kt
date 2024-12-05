package cc.sovellus.vrcaa.ui.components.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import kotlinx.coroutines.launch

@Composable
fun FavoriteEditDialog(
    tag: String,
    onDismiss: () -> Unit,
    onConfirmation: (result: Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val staticName = remember { mutableStateOf(FavoriteManager.getDisplayNameFromTag(tag)) }
    val name = remember { mutableStateOf(FavoriteManager.getDisplayNameFromTag(tag)) }

    val visibility = remember { mutableStateOf("") }
    val options = listOf("private", "public")
    val optionsFormat = mapOf("private" to "Private", "public" to "Public")

    LaunchedEffect(Unit) {
        val metadata = FavoriteManager.getGroupMetadata(tag)

        metadata?.let {
            staticName.value = metadata.displayName
            name.value = metadata.displayName
            visibility.value = metadata.visibility
        }
    }

    AlertDialog(
        modifier = Modifier.padding(16.dp),

        title = {
            Text(text = stringResource(R.string.favorite_edit_title).format(staticName.value))
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(8.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.favorite_edit_display_name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        singleLine = true,
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.favorite_edit_visibility),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    ComboInput(options = options, selection = visibility, optionsFormat)
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        val metadata = FavoriteManager.getGroupMetadata(tag)
                        metadata?.let {
                            val result = FavoriteManager.updateGroupMetadata(tag, metadata.copy(displayName = name.value, visibility = visibility.value))

                            Toast.makeText(
                                context,
                                App.getContext().getString(R.string.favorite_edit_applied),
                                Toast.LENGTH_SHORT
                            ).show()

                            onConfirmation(result)
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.favorite_dialog_button_edit))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    Toast.makeText(
                        context,
                        App.getContext().getString(R.string.favorite_edit_discarded),
                        Toast.LENGTH_SHORT
                    ).show()

                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.favorite_dialog_button_cancel))
            }
        }
    )
}