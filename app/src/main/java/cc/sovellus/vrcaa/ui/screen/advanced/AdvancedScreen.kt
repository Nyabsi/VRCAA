package cc.sovellus.vrcaa.ui.screen.advanced

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import cc.sovellus.vrcaa.ui.screen.network.NetworkLogScreen

class AdvancedScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { AdvancedScreenModel() }

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

                    title = { Text(text = stringResource(R.string.advanced_page_title)) }
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
                                    text = "Networking",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.advanced_page_network_logging)) },
                            supportingContent = { Text(text = stringResource(R.string.advanced_page_network_logging_description)) },
                            trailingContent = {

                                Switch(
                                    checked = model.networkLoggingMode.value,
                                    onCheckedChange = {
                                        model.toggleLogging()
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
                                model.toggleLogging()
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.advanced_page_view_network_logs)) },
                            modifier = Modifier.clickable(
                                onClick = {
                                    navigator.push(NetworkLogScreen())
                                }
                            )
                        )

                        Spacer(modifier = Modifier.padding(4.dp))
                    }

                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "Background Activities",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.advanced_page_battery_optimization)) },
                            supportingContent = { Text(text = stringResource(R.string.advanced_page_battery_optimization_description)) },
                            modifier = Modifier.clickable(onClick = { model.disableBatteryOptimizations() })
                        )
                    }
                }
            },
        )
    }
}