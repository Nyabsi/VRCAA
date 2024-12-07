package cc.sovellus.vrcaa.ui.components.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(title: Int = 0, backButton: Boolean = false) {
    if (title == 0) quickToast(App.getContext(), "Set a name for this tab!")
    val navigator = cafe.adriel.voyager.navigator.LocalNavigator.currentOrThrow
    if (backButton) {
        androidx.compose.material3.TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            title = {
                Text(
                    text = if (title != 0) {
                        stringResource(id = R.string.tabs_label_friends)
                    } else {
                        "Name not set!"
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    } else {
        androidx.compose.material3.TopAppBar(
            title = {
                Text(
                    text = if (title != 0) {
                        stringResource(id = R.string.tabs_label_friends)
                    } else {
                        "Name not set!"
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}