package cc.sovellus.vrcaa.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.launch

@Composable
fun FavoriteDialog(
    type: String,
    id: String,
    metadata: FavoriteManager.FavoriteMetadata,
    onDismiss: () -> Unit,
    onConfirmation: (result: Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val selectedGroup = remember { mutableStateOf("") }
    val groups = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        when (type) {
            "world" -> {
                FavoriteManager.getWorldList().forEach {
                    groups.add(it.key)
                }

                selectedGroup.value = groups[0]
            }
            "avatar" -> {
                FavoriteManager.getAvatarList().forEach {
                    groups.add(it.key)
                }

                selectedGroup.value = groups[0]
            }

            "friend" -> {
                FavoriteManager.getFriendList().forEach {
                    groups.add(it.key)
                }

                selectedGroup.value = groups[0]
            }
        }
    }

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.favorite_dialog_title))
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(8.dp)
            ) {
                item {
                    groups.forEach {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (it == selectedGroup.value),
                                    onClick = { selectedGroup.value = it }),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = it == selectedGroup.value, onClick = {
                                selectedGroup.value = it
                            })
                            Text(text = FavoriteManager.getDisplayNameFromTag(it) ?: it)
                        }
                    }
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
                        val result = FavoriteManager.addFavorite(type, id, selectedGroup.value, metadata)
                        onConfirmation(result)
                    }
                }
            ) {
                Text(stringResource(R.string.favorite_dialog_button_add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(stringResource(R.string.favorite_dialog_button_cancel))
            }
        }
    )
}