package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.developerMode
import cc.sovellus.vrcaa.ui.components.dialog.LogoutDialog
import cc.sovellus.vrcaa.ui.screen.about.AboutScreen

class SettingsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        
        val preferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

        val model = navigator.rememberNavigatorScreenModel { SettingsScreenModel(preferences.developerMode) }

        val logoutState = remember { mutableStateOf(false) }

        if (logoutState.value) {
            LogoutDialog(
                onDismiss = { logoutState.value = false }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .height(172.dp)
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = if (isSystemInDarkTheme()) { painterResource(R.drawable.logo_dark) } else { painterResource(R.drawable.logo_white) },
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        alignment = Alignment.Center
                    )
                }
            }

            if (BuildConfig.DEBUG) {
                item {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.about_page_developer_mode)) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Construction,
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = model.developerMode.value,
                                onCheckedChange = { state ->
                                    model.developerMode.value = state
                                    preferences.developerMode = state

                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.developer_mode_toggle_toast),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                )
                            )
                        },
                        modifier = Modifier.clickable {
                            model.developerMode.value = !model.developerMode.value
                            preferences.developerMode = model.developerMode.value

                            Toast.makeText(
                                context,
                                context.getString(R.string.developer_mode_toggle_toast),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.about_page_translate_title)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Translate,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://crowdin.com/project/vrcaa")
                            )
                            context.startActivity(intent)
                        }
                    )
                )

                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 0.5.dp
                )
            }
            item {

                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_item_about)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable(
                        onClick = {
                            navigator.parent?.parent?.push(AboutScreen())
                        }
                    )
                )
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_item_logout)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable(
                        onClick = {
                            logoutState.value = true
                        }
                    )
                )
            }
        }
    }
}