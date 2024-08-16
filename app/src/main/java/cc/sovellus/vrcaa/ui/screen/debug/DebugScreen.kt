package cc.sovellus.vrcaa.ui.screen.debug

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.toUpperCase
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

class DebugScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { DebugScreenModel() }

        val options = stringArrayResource(R.array.debug_selection_options)
        val icons = listOf(Icons.Filled.NetworkWifi, Icons.Filled.RssFeed)

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

    @Composable
    fun ShowHTTPTraffic(model: DebugScreenModel) {
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
                    Text(text = it.url)
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
                    }
                )
            }
        }
    }

    @Composable
    fun ShowPipelineTraffic(model: DebugScreenModel) {
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
                        Text(text = "Click to view the payload.")
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
                    }
                )
            }
        }
    }
}