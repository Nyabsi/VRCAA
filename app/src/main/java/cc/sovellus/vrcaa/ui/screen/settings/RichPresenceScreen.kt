package cc.sovellus.vrcaa.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.NotificationManager
import cc.sovellus.vrcaa.ui.components.input.PasswordInput
import cc.sovellus.vrcaa.ui.components.input.TextInput
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class RichPresenceScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { RichPresenceScreenModel(context) }

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
                                headlineContent = { Text("Rich Presence") },
                                supportingContent = { Text("Let the world know where you are!") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Filled.Image,
                                        contentDescription = "Localized description"
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
                                        navigator.push(LicensesScreen())
                                    }
                                )
                            )
                            ListItem(
                                headlineContent = { Text("Logout") },
                                supportingContent = { Text("Revoke your discord token from VRCAA") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Localized description"
                                    )
                                },
                                modifier = Modifier.clickable(
                                    onClick = {
                                        model.revoke()
                                    }
                                )
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 16.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TextInput(title = "Username", input = model.username)
                                PasswordInput(title = "Password", input = model.password, visible = model.visibility.value, onVisibilityChange = { model.visibility.value = !model.visibility.value })

                                Button(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .width(200.dp),
                                    onClick = {
                                        model.login()
                                    }
                                ) {
                                    Text(text = stringResource(R.string.login_button_text))
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}