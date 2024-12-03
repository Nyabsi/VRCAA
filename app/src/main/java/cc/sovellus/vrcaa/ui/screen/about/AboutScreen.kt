package cc.sovellus.vrcaa.ui.screen.about

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.misc.Logo
import cc.sovellus.vrcaa.ui.screen.licenses.LicensesScreen

class AboutScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        ),
                ) {
                    Spacer(modifier = Modifier.padding(8.dp))

                    Logo(size = 128.dp)

                    Spacer(modifier = Modifier.padding(8.dp))

                    ListItem(
                        headlineContent = {
                            Text("Version")
                        },
                        supportingContent = {
                            Text(text = "${BuildConfig.VERSION_NAME} ${BuildConfig.FLAVOR} (${BuildConfig.GIT_BRANCH}, ${BuildConfig.GIT_HASH})")
                        }
                    )

                    ListItem(
                        headlineContent = {
                            Text("Model")
                        },
                        supportingContent = {
                            Text(text = Build.MODEL)
                        }
                    )

                    ListItem(
                        headlineContent = {
                            Text("Vendor")
                        },
                        supportingContent = {
                            Text(text = Build.MANUFACTURER)
                        }
                    )

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
        )
    }
}