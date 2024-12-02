package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import cc.sovellus.vrcaa.extension.richPresenceWarningAcknowledged
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ButtonItemWithIcon
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.DividerH
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
        val snackbarHostState = remember { SnackbarHostState() }
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
                        painter = if (App.isAppInDarkTheme()) { painterResource(R.drawable.logo_dark) } else { painterResource(R.drawable.logo_white) },
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        alignment = Alignment.Center
                    )
                }
            }
            item {
                SnackbarHost(hostState = snackbarHostState)
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(
                        message = "This is a custom toast!",
                        duration = SnackbarDuration.Short
                    )
                }
            }
            DividerH()

            ButtonItemWithIcon(R.string.about, R.string.settings_item_about_description, Icons.Outlined.Info) {
                navigator.parent?.parent?.push(AboutScreen())
            }
            ButtonItemWithIcon(R.string.settings_item_theming, R.string.settings_item_theming_description, Icons.Outlined.ImagesearchRoller) {
                navigator.parent?.parent?.push(ThemeScreen())
            }
            ButtonItemWithIcon(R.string.settings_item_rich_presence, R.string.settings_item_rich_presence_description, Icons.Outlined.Image) {
                if (model.preferences.richPresenceWarningAcknowledged)
                    navigator.parent?.parent?.push(RichPresenceScreen())
                else
                    dialogState.value = true
            }
            ButtonItemWithIcon(R.string.settings_item_advanced_settings, R.string.settings_item_advanced_settings_description, Icons.Outlined.DeveloperMode) {
                navigator.parent?.parent?.push(AdvancedScreen())
            }

            DividerH()

            ButtonItemWithIcon(R.string.about_page_translate_title, R.string.about_page_translate_description, Icons.Outlined.Translate) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://crowdin.com/project/vrcaa")
                )
                context.startActivity(intent)
            }
            ButtonItemWithIcon(R.string.settings_item_logout, R.string.settings_item_logout_description, Icons.AutoMirrored.Outlined.Logout) {
                logoutState.value = true
            }
        }
    }
}