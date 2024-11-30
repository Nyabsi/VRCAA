package cc.sovellus.vrcaa.ui.screen.about

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.ui.screen.licenses.LicensesScreen

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
                                contentDescription = null
                            )
                        }
                    },

                    title = { Text(text = stringResource(R.string.about_page_title)) }
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        ),
                ) {
                    item {
                        Spacer(modifier = Modifier.padding(8.dp))

                        Box(
                            modifier = Modifier
                                .height(128.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Image(
                                painter = if (isSystemInDarkTheme() && App.getContext().getSharedPreferences("vrcaa_prefs", 0).currentThemeOption == 1) { painterResource(R.drawable.logo_dark) } else { painterResource(R.drawable.logo_white) },
                                contentDescription = null,
                                contentScale = ContentScale.FillHeight,
                                alignment = Alignment.Center
                            )
                        }

                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                    item {
                        ListItem(
                            headlineContent = {
                                Text("Version")
                            },
                            supportingContent = {
                                Text(text = "${BuildConfig.VERSION_NAME} ${BuildConfig.FLAVOR} (${BuildConfig.GIT_BRANCH}, ${BuildConfig.GIT_HASH})")
                            }
                        )
                    }
                    item {
                        ListItem(
                            headlineContent = {
                                Text("Model")
                            },
                            supportingContent = {
                                Text(text = Build.MODEL)
                            }
                        )
                    }
                    item {
                        ListItem(
                            headlineContent = {
                                Text("Vendor")
                            },
                            supportingContent = {
                                Text(text = Build.MANUFACTURER)
                            }
                        )
                    }
                    item {
                        ListItem(
                            headlineContent = {
                                Text("System Version")
                            },
                            supportingContent = {
                                Text(text = "Android ${Build.VERSION.RELEASE}")
                            }
                        )

                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }
                    item {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.about_page_open_source_licenses_title)) },
                            modifier = Modifier.clickable(
                                onClick = {
                                    navigator.push(LicensesScreen())
                                }
                            )
                        )
                    }
                }
            }
        )
    }
}