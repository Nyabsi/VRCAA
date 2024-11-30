package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.richPresenceWarningAcknowledged
import cc.sovellus.vrcaa.ui.components.dialog.DisclaimerDialog
import cc.sovellus.vrcaa.ui.components.dialog.LogoutDialog
import cc.sovellus.vrcaa.ui.screen.about.AboutScreen
import cc.sovellus.vrcaa.ui.screen.advanced.AdvancedScreen
import cc.sovellus.vrcaa.ui.screen.presence.RichPresenceScreen
import cc.sovellus.vrcaa.ui.screen.theme.ThemeScreen

class SettingsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { SettingsScreenModel() }

        val dialogState = remember { mutableStateOf(false) }
        val logoutState = remember { mutableStateOf(false) }

        val title = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("Notice!")
            }
            append(" ")
            append("Are you sure?")
        }

        val description = buildAnnotatedString {
            append("This feature is not ")
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("condoned")
            }
            append(" nor supported by discord, it may bear ")
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("consequences")
            }
            append(", that may get your account ")
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("terminated")
            }
            append(", if you understand the ")
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("risks")
            }
            append(" press \"Continue\" to continue, or press \"Go Back\".")
        }

        if (dialogState.value) {
            DisclaimerDialog(
                onDismiss = { dialogState.value = false },
                onConfirmation = {
                    dialogState.value = false
                    model.preferences.richPresenceWarningAcknowledged = true
                    navigator.parent?.parent?.push(RichPresenceScreen())
                },
                title,
                description
            )
        }
        
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
                        painter = if (isSystemInDarkTheme() && App.getContext().getSharedPreferences("vrcaa_prefs", 0).currentThemeOption == 1) { painterResource(R.drawable.logo_dark) } else { painterResource(R.drawable.logo_white) },
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        alignment = Alignment.Center
                    )
                }
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
                    supportingContent = { Text(text = stringResource(R.string.settings_item_about_description)) },
                    modifier = Modifier.clickable(
                        onClick = {
                            navigator.parent?.parent?.push(AboutScreen())
                        }
                    )
                )
            }
            item {

                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_item_theming)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.ImagesearchRoller,
                            contentDescription = null
                        )
                    },
                    supportingContent = { Text(text = stringResource(R.string.settings_item_theming_description)) },
                    modifier = Modifier.clickable(
                        onClick = {
                            navigator.parent?.parent?.push(ThemeScreen())
                        }
                    )
                )
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_item_rich_presence)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null
                        )
                    },
                    supportingContent = { Text(text = stringResource(R.string.settings_item_rich_presence_description)) },
                    modifier = Modifier.clickable(
                        onClick = {
                            if (model.preferences.richPresenceWarningAcknowledged)
                                navigator.parent?.parent?.push(RichPresenceScreen())
                            else
                                dialogState.value = true
                        }
                    )
                )
            }
            item {

                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_item_advanced_settings)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.DeveloperMode,
                            contentDescription = null
                        )
                    },
                    supportingContent = { Text(text = stringResource(R.string.settings_item_advanced_settings_description)) },
                    modifier = Modifier.clickable(
                        onClick = {
                            navigator.parent?.parent?.push(AdvancedScreen())
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