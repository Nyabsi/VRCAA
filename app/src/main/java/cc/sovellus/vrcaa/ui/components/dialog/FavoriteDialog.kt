package cc.sovellus.vrcaa.ui.components.dialog

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.launch

@Composable
fun FavoriteDialog(
    type: IFavorites.FavoriteType,
    id: String,
    metadata: FavoriteManager.FavoriteMetadata,
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val selectedGroup = remember { mutableStateOf("") }
    val groups = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        when (type) {
            IFavorites.FavoriteType.FAVORITE_WORLD -> {
                FavoriteManager.getWorldList().forEach {
                    groups.add(it.key)
                }

                selectedGroup.value = groups[0]
            }
            IFavorites.FavoriteType.FAVORITE_AVATAR -> {
                FavoriteManager.getAvatarList().forEach {
                    groups.add(it.key)
                }

                selectedGroup.value = groups[0]
            }

            IFavorites.FavoriteType.FAVORITE_FRIEND -> {
                FavoriteManager.getFriendList().forEach {
                    groups.add(it.key)
                }

                selectedGroup.value = groups[0]
            }

            IFavorites.FavoriteType.FAVORITE_NONE -> {   }
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
                            Text(text = FavoriteManager.getDisplayNameFromTag(it))
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

                        if (result) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.favorite_toast_favorite_added).format(metadata.name),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.favorite_toast_favorite_added_failed).format(metadata.name),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        onConfirmation()
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