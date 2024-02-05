package cc.sovellus.vrcaa.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R

class AboutScreen : Screen {

    override val key = uniqueScreenKey

    @SuppressLint("BatteryLife")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

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

                    title = { Text(stringResource(R.string.about_page_title)) }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.app_name)) }
                        )
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.about_page_version_title).format(BuildConfig.VERSION_NAME)) },
                            supportingContent = { Text(stringResource(R.string.about_page_version_description)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Localized description"
                                )
                            },
                            modifier = Modifier.clickable(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.about_page_version_toast_message),
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                            headlineContent = { Text(stringResource(R.string.about_page_open_source_licenses_title)) },
                            supportingContent = { Text(stringResource(R.string.about_page_open_source_licenses_description)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Dehaze,
                                    contentDescription = "Localized description"
                                )
                            },
                            modifier = Modifier.clickable(
                                onClick = {
                                    navigator.push(LicensesScreen())
                                }
                            )
                        )
                    }
                    item {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.about_page_translate_title)) },
                            supportingContent = { Text(stringResource(R.string.about_page_translate_description)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Language,
                                    contentDescription = "Localized description"
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
                            headlineContent = { Text(stringResource(R.string.about_page_battery_optimizations_title))  },
                            supportingContent = { Text(stringResource(R.string.about_page_battery_optimizations_description)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Localized description"
                                )
                            },
                            modifier = Modifier.clickable(
                                onClick = {
                                    val pm: PowerManager = getSystemService(context, PowerManager::class.java)!!
                                    if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
                                        val intent = Intent(
                                            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.about_page_battery_optimizations_toast),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        )
                    }
                }
            }
        )
    }
}