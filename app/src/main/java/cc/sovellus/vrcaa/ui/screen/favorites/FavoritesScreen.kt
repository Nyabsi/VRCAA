package cc.sovellus.vrcaa.ui.screen.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.layout.HorizontalRow
import cc.sovellus.vrcaa.ui.components.layout.RowItem
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen

class FavoritesScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.rememberNavigatorScreenModel { FavoritesScreenModel() }

        val worldList = model.worldList.collectAsState()
        val avatarList = model.avatarList.collectAsState()

        val options = stringArrayResource(R.array.favorites_selection_options)
        val icons = listOf(Icons.Filled.Cabin, Icons.Filled.Person)

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
                    title = {
                        Text(text = stringResource(R.string.favorite_page_title))
                    }
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
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

                    Spacer(modifier = Modifier.padding(4.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        item {
                            when (model.currentIndex.intValue) {
                                0 -> ShowWorlds(worldList)
                                1 -> ShowAvatars(avatarList)
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun ShowWorlds(worldList: State<SnapshotStateMap<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>>) {
        val navigator = LocalNavigator.currentOrThrow

        val sortedWorldList = worldList.value.toSortedMap(compareBy { it.substring(6).toInt() })
        sortedWorldList.forEach { item ->
            if (item.value.size > 0) {
                HorizontalRow(
                    title = FavoriteManager.getDisplayNameFromTag(item.key) ?: item.key
                ) {
                    items(item.value) {
                        RowItem(name = it.name, count = null, url = it.thumbnailUrl) {
                            if (it.name != "???") {
                                navigator.push(WorldInfoScreen(it.id))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }

    @Composable
    fun ShowAvatars(avatarList: State<SnapshotStateMap<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>>) {
        val navigator = LocalNavigator.currentOrThrow

        val sortedAvatarList = avatarList.value.toSortedMap(compareBy { it.substring(7).toInt() })
        sortedAvatarList.forEach { item ->
            if (item.value.size > 0) {
                HorizontalRow(
                    title = FavoriteManager.getDisplayNameFromTag(item.key) ?: item.key
                ) {
                    items(item.value) {
                        RowItem(name = it.name, count = null, url = it.thumbnailUrl) {
                            if (it.name != "???") {
                                navigator.push(AvatarScreen(it.id))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}