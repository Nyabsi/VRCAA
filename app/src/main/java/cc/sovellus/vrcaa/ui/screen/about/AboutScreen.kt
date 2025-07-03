/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
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
        val model = navigator.rememberNavigatorScreenModel { AboutScreenModel() }

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
                        headlineContent = { Text("Crash Analytics") },
                        supportingContent = { Text("Sends anonymous statistics for application crashes, to help with development.") },
                        trailingContent = {
                            Switch(
                                checked = model.crashAnalytics.value,
                                onCheckedChange = {
                                    model.toggleAnalytics()
                                },

                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        },

                        modifier = Modifier.clickable {
                            model.toggleAnalytics()
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