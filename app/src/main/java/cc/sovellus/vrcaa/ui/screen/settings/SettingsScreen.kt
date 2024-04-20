package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Context.MODE_PRIVATE
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
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Update
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.richPresenceWarningAcknowledged
import cc.sovellus.vrcaa.ui.components.dialog.DisclaimerDialog
import cc.sovellus.vrcaa.ui.models.settings.SettingsModel
import cc.sovellus.vrcaa.ui.screen.about.AboutScreen
import cc.sovellus.vrcaa.ui.screen.presence.RichPresenceScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class SettingsScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val preferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

        val model = navigator.rememberNavigatorScreenModel { SettingsModel(context) }
        val dialogState = remember { mutableStateOf(false) }

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
                    preferences.richPresenceWarningAcknowledged = true
                    navigator.parent?.parent?.push(RichPresenceScreen())
                },
                title,
                description
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
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_item_check_updates)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Update,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = model.enableUpdates.value,
                            onCheckedChange = { state -> model.toggleUpdate(state) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        )
                    }
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
                            contentDescription = "Localized description"
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
                    headlineContent = { Text(stringResource(R.string.settings_item_rich_presence)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable(
                        onClick = {
                            if (preferences.richPresenceWarningAcknowledged)
                                navigator.parent?.parent?.push(RichPresenceScreen())
                            else
                                dialogState.value = true
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
                            model.doLogout()
                        }
                    )
                )
            }
        }
    }
}