package cc.sovellus.vrcaa.ui.screen.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreen
import cc.sovellus.vrcaa.ui.screen.settings.SettingsScreen
import cc.sovellus.vrcaa.ui.tabs.FriendsTab
import cc.sovellus.vrcaa.ui.tabs.HomeTab
import cc.sovellus.vrcaa.ui.tabs.NotificationsTab
import cc.sovellus.vrcaa.ui.tabs.ProfileTab

class MainScreen : Screen {

    override val key: ScreenKey
        get() = "main"

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator: Navigator = LocalNavigator.currentOrThrow
        val context: Context = LocalContext.current

        val screenModel = navigator.rememberNavigatorScreenModel { MainScreenModel(context) }

        TabNavigator(
            HomeTab,
            tabDisposable = {
                TabDisposable(
                    navigator = it,
                    tabs = listOf(HomeTab, FriendsTab, NotificationsTab, ProfileTab)
                )
            }
        ) {
            Scaffold(
                topBar = {
                    SearchBar(
                        query = screenModel.searchText.value,
                        placeholder = { Text(text = stringResource(R.string.main_search_placeholder)) },
                        onQueryChange = { screenModel.searchText.value = it; },
                        onSearch = {
                            screenModel.existSearchMode()
                            navigator.push(SearchResultScreen(screenModel.searchText.value))
                        },
                        active = screenModel.isSearchActive.value,
                        onActiveChange = {
                            if (it) { screenModel.enterSearchMode() } else { screenModel.existSearchMode() }
                        },
                        tonalElevation = screenModel.tonalElevation.value,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        trailingIcon = {
                            if (screenModel.isSearchActive.value) {
                                IconButton(onClick = { screenModel.clearSearchText() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = stringResource(R.string.preview_image_description)
                                    )
                                }
                            } else {
                                IconButton(onClick = { navigator.push(SettingsScreen()) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = stringResource(R.string.preview_image_description)
                                    )
                                }
                            }
                        },
                        leadingIcon = {
                            if (screenModel.isSearchActive.value) {
                                IconButton(onClick = { screenModel.existSearchMode() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.preview_image_description)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = stringResource(R.string.preview_image_description)
                                )
                            }
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(screenModel.searchHistory.size) {
                                val item = screenModel.searchHistory[it]
                                ListItem(
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Filled.History,
                                            contentDescription = stringResource(R.string.preview_image_description)
                                        )
                                    },
                                    headlineContent = {
                                        Text(text = item)
                                    },
                                    modifier = Modifier.clickable(
                                        onClick = {
                                            screenModel.existSearchMode()
                                            navigator.push(SearchResultScreen(item))
                                        }
                                    )
                                )
                            }
                        }
                    }
                },
                content = {
                    Column(
                        modifier = Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    ) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    NavigationBar(
                        tonalElevation = screenModel.tonalElevation.value
                    ) {
                        NavigationBarItem(HomeTab)
                        NavigationBarItem(FriendsTab)
                        NavigationBarItem(NotificationsTab)
                        NavigationBarItem(ProfileTab)
                    }
                }
            )
        }
    }

    @Composable
    private fun RowScope.NavigationBarItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        NavigationBarItem(
            selected = tabNavigator.current.key == tab.key,
            onClick = { tabNavigator.current = tab },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            label = { Text(text = tab.options.title) }
        )
    }
}