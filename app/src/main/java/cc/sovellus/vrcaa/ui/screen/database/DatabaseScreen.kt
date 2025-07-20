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

package cc.sovellus.vrcaa.ui.screen.database

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import java.time.LocalDateTime

class DatabaseScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { DatabaseScreenModel() }

        val backupLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/octet-stream")
        ) { uri ->
            uri?.let {
                model.backupDatabaseToUri(uri)
            }
        }

        val restoreLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {
                model.restoreDatabaseFromUri(uri)
            }
        }

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

                    title = { Text(text = stringResource(R.string.database_page_title)) }
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        )
                ) {
                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.database_page_section_statistics),
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = {
                                Text(stringResource(R.string.database_page_statistics_size))
                            },
                            supportingContent = {
                                Text(text = model.getDatabaseSizeReadable())
                            }
                        )

                        ListItem(
                            headlineContent = {
                                Text(stringResource(R.string.database_page_statistics_row_count))
                            },
                            supportingContent = {
                                Text(text = model.getDatabaseRowsReadable())
                            }
                        )

                        Spacer(modifier = Modifier.padding(4.dp))
                    }

                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.database_page_section_recovery),
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.database_page_recovery_backup)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.Backup,
                                    contentDescription = null
                                )
                            },
                            supportingContent = { Text(stringResource(R.string.database_page_recovery_backup_description)) },
                            modifier = Modifier.clickable(
                                onClick = {
                                    backupLauncher.launch("VRCAA-backup-${LocalDateTime.now()}.db")
                                }
                            )
                        )

                        ListItem(
                            headlineContent = { Text(stringResource(R.string.database_page_recovery_restore)) },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.Restore,
                                    contentDescription = null
                                )
                            },
                            supportingContent = { Text(stringResource(R.string.database_page_recovery_restore_description)) },
                            modifier = Modifier.clickable(
                                onClick = {
                                    restoreLauncher.launch(arrayOf("application/octet-stream"))
                                }
                            )
                        )

                        Spacer(modifier = Modifier.padding(4.dp))
                    }

                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.database_page_section_glide),
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = {
                                Text(stringResource(R.string.database_page_glide_cache_size))
                            },
                            supportingContent = {
                                Text(text = model.getGlideCacheSizeReadable())
                            }
                        )

                        ListItem(
                            headlineContent = { Text(stringResource(R.string.database_page_glide_clean_cache)) },
                            modifier = Modifier.clickable(
                                onClick = {
                                    model.cleanGlideCache()
                                }
                            )
                        )

                        ListItem(
                            headlineContent = {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(R.string.database_page_glide_clean_cache_description),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            },
                        )
                    }
                }
            },
        )
    }
}