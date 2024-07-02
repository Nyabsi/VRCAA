package cc.sovellus.vrcaa.ui.screen.presence

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.dialog.InputDialog
import cc.sovellus.vrcaa.ui.components.input.CodeInput
import cc.sovellus.vrcaa.ui.components.input.PasswordInput
import cc.sovellus.vrcaa.ui.components.input.TextInput

class RichPresenceScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { RichPresenceScreenModel(context) }
        val dialogState = remember { mutableStateOf(false) }

        if (dialogState.value) {
            InputDialog(
                onDismiss = {
                    dialogState.value = false
                },
                onConfirmation = {
                    dialogState.value = false
                    model.setWebSocket()
                },
                title = "Set Webhook Url",
                text = model.websocket
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },

                    title = { Text(text = "Rich Presence") }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        if (model.token.value.isNotEmpty()) {
                            ListItem(
                                headlineContent = { Text(stringResource(R.string.discord_login_label_enable)) },
                                supportingContent = { Text(stringResource(R.string.discord_login_description_enable)) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Filled.Image,
                                        contentDescription = null
                                    )
                                },
                                trailingContent = {
                                    Switch(
                                        checked = model.enabled.value,
                                        onCheckedChange = {
                                            model.toggle()
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                        ),
                                        enabled = true
                                    )
                                },
                                modifier = Modifier.clickable(
                                    onClick = {
                                        model.toggle()
                                    }
                                )
                            )
                            ListItem(
                                headlineContent = { Text(stringResource(R.string.discord_login_label_set_webhook)) },
                                supportingContent = { Text(stringResource(R.string.discord_login_description_set_webhook)) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Filled.ImageSearch,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.clickable(
                                    onClick = {
                                        dialogState.value = true
                                    }
                                )
                            )
                            ListItem(
                                headlineContent = { Text(stringResource(R.string.discord_login_label_logout)) },
                                supportingContent = { Text(stringResource(R.string.discord_login_description_logout)) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.clickable(
                                    onClick = {
                                        model.revoke()
                                    }
                                )
                            )
                        } else {
                            if (model.mfa.value) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = stringResource(R.string.discord_login_title_verify))

                                    CodeInput(input = model.code)

                                    Button(
                                        modifier = Modifier
                                            .height(48.dp)
                                            .width(200.dp),
                                        onClick = {
                                            model.mfa()
                                        }
                                    ) {
                                        Text(text = stringResource(R.string.discord_login_button_verify))
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = stringResource(R.string.discord_login_title))

                                    TextInput(title = stringResource(R.string.discord_login_label_username), input = model.username)
                                    PasswordInput(title = stringResource(R.string.discord_login_label_password), input = model.password, visible = model.visibility.value, onVisibilityChange = { model.visibility.value = !model.visibility.value })

                                    Button(
                                        modifier = Modifier
                                            .height(48.dp)
                                            .width(200.dp),
                                        onClick = {
                                            model.login()
                                        }
                                    ) {
                                        Text(text = stringResource(R.string.discord_login_button_login))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}