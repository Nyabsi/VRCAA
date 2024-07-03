package cc.sovellus.vrcaa.ui.screen.navigation

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.ApiManager.cache
import cc.sovellus.vrcaa.ui.components.dialog.ProfileEditDialog
import cc.sovellus.vrcaa.ui.components.dialog.UpdatedDialog
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreen
import cc.sovellus.vrcaa.ui.tabs.FeedTab
import cc.sovellus.vrcaa.ui.tabs.FriendsTab
import cc.sovellus.vrcaa.ui.tabs.HomeTab
import cc.sovellus.vrcaa.ui.tabs.PicturesTab
import cc.sovellus.vrcaa.ui.tabs.ProfileTab
import cc.sovellus.vrcaa.ui.tabs.SettingsTab
import kotlinx.coroutines.launch

class NavigationScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator: Navigator = LocalNavigator.currentOrThrow
        val context: Context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { NavigationScreenModel(context) }

        if (model.hasUpdate.value) {
            UpdatedDialog(
                onDismiss = {
                    model.hasUpdate.value = false
                },
                onConfirmation = {
                    model.hasUpdate.value = false
                    model.update(context)
                },
                title = stringResource(R.string.update_dialog_title),
                description = stringResource(R.string.update_dialog_description)
            )
        }

        TabNavigator(
            HomeTab,
            tabDisposable = {
                TabDisposable(
                    navigator = it,
                    tabs = listOf(HomeTab, FriendsTab, FeedTab, ProfileTab)
                )
            }
        ) {
            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }
            var isMenuExpanded by remember { mutableStateOf(false) }
            var isEditingProfile by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()

            Scaffold(
                topBar = {
                    if (it.current.options.index.toInt() == 0) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SearchBar(
                                query = model.searchText.value,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.main_search_placeholder),
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onQueryChange = { model.searchText.value = it; },
                                onSearch = {
                                    model.existSearchMode()
                                    navigator.push(SearchResultScreen(model.searchText.value))
                                    model.clearSearchText()
                                },
                                active = model.searchModeActivated.value,
                                onActiveChange = {
                                    if (it) {
                                        model.enterSearchMode()
                                    } else {
                                        model.existSearchMode()
                                    }
                                },
                                trailingIcon = {
                                    if (model.searchModeActivated.value) {
                                        IconButton(onClick = { model.clearSearchText() }) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = null                                        )
                                        }
                                    } else {
                                        IconButton(onClick = { showBottomSheet = true }) {
                                            Icon(
                                                imageVector = Icons.Filled.MoreVert,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                },
                                leadingIcon = {
                                    if (model.searchModeActivated.value) {
                                        IconButton(onClick = { model.existSearchMode() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = null
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = null
                                        )
                                    }
                                }
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(model.searchHistory.size) {
                                        val item = model.searchHistory.reversed()[it]
                                        ListItem(
                                            leadingContent = {
                                                Icon(
                                                    imageVector = Icons.Filled.History,
                                                    contentDescription = null
                                                )
                                            },
                                            headlineContent = {
                                                Text(text = item)
                                            },
                                            modifier = Modifier.clickable(
                                                onClick = {
                                                    model.existSearchMode()
                                                    navigator.push(SearchResultScreen(item))
                                                }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else if (it.current.options.index.toInt() == 1) {
                        TopAppBar(
                            title = { Text(
                                text = stringResource(id = R.string.tabs_label_friends),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) }
                        )
                    }
                    else if (it.current.options.index.toInt() == 2) {
                        TopAppBar(
                            actions = {
                                IconButton(onClick = { isMenuExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = null
                                    )
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        DropdownMenu(
                                            expanded = isMenuExpanded,
                                            onDismissRequest = { isMenuExpanded = false },
                                            offset = DpOffset(0.dp, 0.dp)
                                        ) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    isEditingProfile = true
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.profile_edit_dialog_title_edit_profile)) }
                                            )
                                            DropdownMenuItem(
                                                onClick = {
                                                    cache.getProfile()?.let {
                                                        navigator.push(
                                                            UserGroupsScreen(it.displayName, it.id)
                                                        )
                                                    }
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.user_dropdown_view_groups)) }
                                            )
                                        }
                                    }
                                }
                            },
                            title = { Text(
                                text = stringResource(id = R.string.tabs_label_profile),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) }
                        )
                    }
                    else if (it.current.options.index.toInt() == 3) {
                        TopAppBar(
                            title = { Text(
                                text = stringResource(id = R.string.tabs_label_feed),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) }
                        )
                    }
                    else if (it.current.options.index.toInt() == 4) {
                        TopAppBar(
                            title = { Text(
                                text = stringResource(id = R.string.tabs_label_pictures),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) }
                        )
                    }
                    else if (it.current.options.index.toInt() == 5) {
                        TopAppBar(
                            title = { Text(
                                text = stringResource(id = R.string.tabs_label_settings),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) }
                        )
                    }
                },
                content = { padding ->
                    Column(
                        modifier = Modifier.padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        )
                    ) {
                        if (isEditingProfile) {
                            ProfileEditDialog(
                                onDismiss = { isEditingProfile = false },
                                onConfirmation = {
                                    isEditingProfile = false
                                },
                                title = stringResource(R.string.profile_edit_dialog_title_edit_profile)
                            )
                        }

                        CurrentTab()
                    }

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState
                        ) {
                            LazyColumn {
                                item {
                                    ListItem(leadingContent = {
                                        OutlinedButton(onClick = {
                                            model.resetSettings()
                                        }) {
                                            Text(stringResource(R.string.search_filter_button_reset))
                                        }
                                    }, trailingContent = {
                                        Button(onClick = {
                                            scope.launch {
                                                model.applySettings()
                                                sheetState.hide()
                                            }.invokeOnCompletion {
                                                if (!sheetState.isVisible) {
                                                    showBottomSheet = false
                                                }
                                            }
                                        }) {
                                            Text(stringResource(R.string.search_filter_button_apply))
                                        }
                                    }, headlineContent = { })
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_worlds)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.Cabin,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    HorizontalDivider(
                                        color = Color.Gray,
                                        thickness = 0.5.dp
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_worlds_featured)) },
                                        trailingContent = {
                                            Switch(
                                                checked = model.featuredWorlds.value,
                                                onCheckedChange = { state ->
                                                    run {
                                                        model.featuredWorlds.value = state
                                                    }
                                                },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                                )
                                            )
                                        }
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_worlds_sort_by)) },
                                        trailingContent = {
                                            val options = listOf("popularity", "heat", "trust", "shuffle", "random", "favorites", " reportScore", "reportCount", "publicationDate", "labsPublicationDate", "created", "updated", "order", "relevance", "magic", "name")
                                            ComboInput(options = options, selection = model.sortWorlds)
                                        }
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_worlds_count)) },
                                        trailingContent = {
                                            OutlinedTextField(
                                                value = model.worldsAmount.intValue.toString(),
                                                onValueChange = {
                                                    model.worldsAmount.intValue = it.toIntOrNull() ?: 0
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_users)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.People,
                                                contentDescription = null                                            )
                                        }
                                    )
                                    HorizontalDivider(
                                        color = Color.Gray,
                                        thickness = 0.5.dp
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_users_count)) },
                                        trailingContent = {
                                            OutlinedTextField(
                                                value = model.usersAmount.intValue.toString(),
                                                onValueChange = {
                                                    model.usersAmount.intValue = it.toIntOrNull() ?: model.usersAmount.intValue
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_groups)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.Groups,
                                                contentDescription = null                                            )
                                        }
                                    )
                                    HorizontalDivider(
                                        color = Color.Gray,
                                        thickness = 0.5.dp
                                    )
                                }
                                item {
                                    ListItem(
                                        headlineContent = { Text(stringResource(R.string.search_filter_category_groups_count)) },
                                        trailingContent = {
                                            OutlinedTextField(
                                                value = model.groupsAmount.intValue.toString(),
                                                onValueChange = {
                                                    model.groupsAmount.intValue = it.toIntOrNull() ?: model.groupsAmount.intValue
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(HomeTab)
                        NavigationBarItem(FriendsTab)
                        NavigationBarItem(FeedTab)
                        if (BuildConfig.FLAVOR == "quest") { NavigationBarItem(PicturesTab) }
                        NavigationBarItem(ProfileTab)
                        NavigationBarItem(SettingsTab)
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