package cc.sovellus.vrcaa.ui.screen.network

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.DebugManager
import java.util.Locale

class NetworkLogScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { NetworkLogScreenModel() }

        val options = stringArrayResource(R.array.debug_selection_options)
        val icons = listOf(Icons.Filled.NetworkWifi, Icons.Filled.RssFeed)

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

                    title = { Text(text = stringResource(R.string.tabs_label_debug)) }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        )
                ) {
                    MultiChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                icon = {
                                    SegmentedButtonDefaults.Icon(active = index == model.currentIndex.intValue) {
                                        Icon(
                                            imageVector = icons[index],
                                            contentDescription = null,
                                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                        )
                                    }
                                },
                                onCheckedChange = {
                                    model.currentIndex.intValue = index
                                },
                                checked = index == model.currentIndex.intValue
                            ) {
                                Text(text = label, softWrap = true, maxLines = 1)
                            }
                        }
                    }

                    when (model.currentIndex.intValue) {
                        0 -> ShowHTTPTraffic(model)
                        1 -> ShowPipelineTraffic(model)
                    }
                }
            }
        )
    }

    @Composable
    fun ShowHTTPTraffic(model: NetworkLogScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val metadata = model.metadata.collectAsState()

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(1.dp),
            state = rememberLazyListState()
        ) {
            items(metadata.value.filter { it.type == DebugManager.DebugType.DEBUG_TYPE_HTTP }) {
                ListItem(
                    overlineContent = {
                        Text(text = it.code.toString())
                    },
                    headlineContent = {
                        if (it.code == 200 || it.code == 304) {
                            Text(
                                text = it.methodType,
                                color = Color.Green
                            )
                        } else {
                            Text(
                                text = it.methodType,
                                color = Color.Red
                            )
                        }
                    }, 
                    supportingContent = {
                        Column {
                            Text(text = it.url, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "Click to view the payload")
                        }
                    },
                    leadingContent = {
                        if (it.code == 200 || it.code == 304) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.offset(y = 24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                                modifier = Modifier.offset(y = 24.dp)
                            )
                        }
                    },
                    trailingContent = {
                        Text(text = "${it.payload.length} bytes")
                    },
                    modifier = Modifier.clickable {
                        navigator.push(PacketViewScreen(it.payload, it.url))
                    }
                )
            }
        }
    }

    @Composable
    fun ShowPipelineTraffic(model: NetworkLogScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val metadata = model.metadata.collectAsState()

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(1.dp),
            state = rememberLazyListState()
        ) {
            items(metadata.value.filter { it.type == DebugManager.DebugType.DEBUG_TYPE_PIPELINE }) {
                ListItem(
                    headlineContent = {
                        if (!it.unknown) {
                            Text(
                                text = it.name.uppercase(Locale.ROOT),
                                color = Color.Green
                            )
                        } else {
                            Text(
                                text = it.name.uppercase(Locale.ROOT),
                                color = Color.Red
                            )
                        }
                    },
                    supportingContent = {
                        Text(text = "Click to view the payload")
                    },
                    leadingContent = {
                        if (!it.unknown) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.offset(y = 4.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                                modifier = Modifier.offset(y = 4.dp)
                            )
                        }
                    },
                    trailingContent = {
                        Text(text = "${it.payload.length} bytes")
                    },
                    modifier = Modifier.clickable {
                        navigator.push(PacketViewScreen(it.payload))
                    }
                )
            }
        }
    }
}