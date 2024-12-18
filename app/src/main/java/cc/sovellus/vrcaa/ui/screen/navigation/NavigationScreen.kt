package cc.sovellus.vrcaa.ui.screen.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
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
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.activity.MainActivity
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.ui.components.dialog.NoInternetDialog
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import cc.sovellus.vrcaa.ui.screen.avatars.AvatarsScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreen
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreen
import cc.sovellus.vrcaa.ui.tabs.DebugTab
import cc.sovellus.vrcaa.ui.tabs.FavoritesTab
import cc.sovellus.vrcaa.ui.tabs.FeedTab
import cc.sovellus.vrcaa.ui.tabs.FriendsTab
import cc.sovellus.vrcaa.ui.tabs.HomeTab
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
        val model = navigator.rememberNavigatorScreenModel { NavigationScreenModel() }

        if (model.hasNoInternet.value) {
            NoInternetDialog(onClick = {
                val bundle = bundleOf()
                bundle.putBoolean("RESTART_SESSION", true)

                val intent = Intent(context, MainActivity::class.java)
                intent.putExtras(bundle)
                context.startActivity(intent)
            })
        }

        val tabs = arrayListOf(HomeTab, FriendsTab, FavoritesTab, FeedTab, ProfileTab, SettingsTab)

        TabNavigator(HomeTab, tabDisposable = {
            TabDisposable(
                navigator = it, tabs = tabs
            )
        }) { tabNavigator ->
            val settingsSheetState = rememberModalBottomSheetState()
            val profileSheetState = rememberModalBottomSheetState()

            var showSettingsSheet by remember { mutableStateOf(false) }
            var isMenuExpanded by remember { mutableStateOf(false) }
            var showProfileSheet by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()

            var pressBackCounter by remember { mutableIntStateOf(0) }

            BackHandler(enabled = true, onBack = {
                if (tabNavigator.current != HomeTab) {
                    tabNavigator.current = HomeTab
                } else {
                    pressBackCounter++
                }

                if (tabNavigator.current == HomeTab) {
                    if (pressBackCounter == 1) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.misc_exit_toast_label),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (pressBackCounter == 2) {
                        if (context is Activity) context.finish()
                    }
                }
            })

            Scaffold(topBar = {
                when (tabNavigator.current.options.index) {
                    HomeTab.options.index -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SearchBar(
                                inputField = {
                                    InputField(query = model.searchText.value,
                                        onQueryChange = { model.searchText.value = it; },
                                        onSearch = {
                                            model.searchModeActivated.value = false
                                            navigator.push(SearchResultScreen(model.searchText.value))
                                            model.addSearchHistory()
                                        },
                                        expanded = model.searchModeActivated.value,
                                        onExpandedChange = {
                                            model.searchModeActivated.value = true
                                        },
                                        enabled = true,
                                        placeholder = {
                                            if (!App.isMinimalistModeEnabled()) {
                                                Text(
                                                    text = stringResource(R.string.main_search_placeholder),
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        },
                                        leadingIcon = {
                                            if (model.searchModeActivated.value) {
                                                IconButton(onClick = {
                                                    model.searchModeActivated.value = false
                                                }) {
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
                                        },
                                        trailingIcon = {
                                            if (model.searchModeActivated.value) {
                                                IconButton(onClick = { model.clearSearchText() }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Close,
                                                        contentDescription = null
                                                    )
                                                }
                                            } else {
                                                IconButton(onClick = {
                                                    showSettingsSheet = true
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.MoreVert,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        })
                                },
                                expanded = model.searchModeActivated.value,
                                onExpandedChange = { },
                                shape = SearchBarDefaults.inputFieldShape,
                                colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                                tonalElevation = if (model.searchModeActivated.value) {
                                    0.dp
                                } else {
                                    8.dp
                                },
                                windowInsets = SearchBarDefaults.windowInsets.exclude(
                                    WindowInsets(left = 4.dp, right = 4.dp)
                                ),
                                content = {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        if (model.searchModeActivated.value) {
                                            items(model.searchHistory.size) {
                                                val item = model.searchHistory.reversed()[it]
                                                ListItem(leadingContent = {
                                                    Icon(
                                                        imageVector = Icons.Filled.History,
                                                        contentDescription = null
                                                    )
                                                }, headlineContent = {
                                                    Text(text = item)
                                                }, modifier = Modifier.clickable(onClick = {
                                                    model.searchModeActivated.value = false
                                                    navigator.push(
                                                        SearchResultScreen(
                                                            item
                                                        )
                                                    )
                                                }))
                                            }
                                        }
                                    }
                                },
                            )
                        }
                    }

                    FriendsTab.options.index -> {
                        TopAppBar(title = {
                            Text(
                                text = stringResource(id = R.string.tabs_label_friends),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                    }

                    ProfileTab.options.index -> {
                        TopAppBar(actions = {
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
                                        DropdownMenuItem(onClick = {
                                            model.getCurrentProfileValues()
                                            showProfileSheet = true
                                            isMenuExpanded = false
                                        },
                                            text = { Text(stringResource(R.string.profile_edit_dialog_title_edit_profile)) })
                                        DropdownMenuItem(onClick = {
                                            CacheManager.getProfile()?.let {
                                                navigator.push(
                                                    UserGroupsScreen(
                                                        it.displayName, it.id
                                                    )
                                                )
                                            }
                                            isMenuExpanded = false
                                        },
                                            text = { Text(stringResource(R.string.user_dropdown_view_groups)) })
                                        DropdownMenuItem(onClick = {
                                            CacheManager.getProfile()?.let {
                                                navigator.push(
                                                    WorldsScreen(
                                                        it.displayName, it.id, true
                                                    )
                                                )
                                            }
                                            isMenuExpanded = false
                                        },
                                            text = { Text(stringResource(R.string.user_dropdown_view_worlds)) })
                                        DropdownMenuItem(onClick = {
                                            navigator.push(AvatarsScreen())
                                            isMenuExpanded = false
                                        },
                                            text = { Text(stringResource(R.string.user_dropdown_view_avatars)) })
                                    }
                                }
                            }
                        }, title = {
                            Text(
                                text = stringResource(id = R.string.tabs_label_profile),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                    }

                    FavoritesTab.options.index -> {
                        TopAppBar(title = {
                            Text(
                                text = stringResource(id = R.string.tabs_label_favorites),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                    }

                    FeedTab.options.index -> {
                        TopAppBar(title = {
                            Text(
                                text = stringResource(id = R.string.tabs_label_feed),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                    }

                    SettingsTab.options.index -> {
                        TopAppBar(title = {
                            Text(
                                text = stringResource(id = R.string.tabs_label_settings),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                    }

                    DebugTab.options.index -> {
                        TopAppBar(title = {
                            Text(
                                text = stringResource(id = R.string.tabs_label_debug),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                    }
                }
            }, content = { padding ->
                Column(
                    modifier = Modifier.padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                ) {
                    CurrentTab()
                }

                if (showProfileSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showProfileSheet = false
                            Toast.makeText(
                                context,
                                context.getString(R.string.profile_edit_dialog_toast_cancelled),
                                Toast.LENGTH_LONG
                            ).show()
                        }, sheetState = profileSheetState
                    ) {
                        LazyColumn {
                            item {
                                ListItem(leadingContent = {
                                    OutlinedButton(onClick = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.profile_edit_dialog_toast_cancelled),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        showProfileSheet = false
                                    }) {
                                        Text("Cancel")
                                    }
                                }, trailingContent = {
                                    Button(onClick = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.profile_edit_dialog_toast_updated),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        scope.launch {
                                            CacheManager.getProfile()?.let {
                                                api.user.updateProfileByUserId(it.id, model.status.value, model.description.value, model.bio.value, model.bioLinks, model.verifiedStatus.value)?.let { user ->
                                                    CacheManager.updateProfile(user)
                                                }
                                            }
                                            profileSheetState.hide()
                                        }.invokeOnCompletion {
                                            if (!settingsSheetState.isVisible) {
                                                showProfileSheet = false
                                            }
                                        }
                                    }) {
                                        Text("Change")
                                    }
                                }, headlineContent = { })
                            }
                            item {
                                ListItem(
                                    headlineContent = {
                                        Text(text = stringResource(R.string.profile_edit_dialog_title_status), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                                    }
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ComboInput(
                                        options = listOf("join me", "active", "ask me", "busy"),
                                        readableOptions = mapOf("join me" to "Join Me", "active" to "Active", "ask me" to "Ask Me", "busy" to "Busy"),
                                        selection = model.status
                                    )
                                }
                            }
                            item {
                                ListItem(
                                    headlineContent = {
                                        Text(text = stringResource(R.string.profile_edit_dialog_title_status_description), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                                    }
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp),
                                        value = model.description.value,
                                        onValueChange = {
                                            model.description.value = it
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                    )
                                }
                            }

                            item {
                                ListItem(
                                    headlineContent = {
                                        Text(text = stringResource(R.string.profile_edit_dialog_title_bio), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                                    }
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp),
                                        value = model.bio.value,
                                        onValueChange = {
                                            model.bio.value = it
                                        },
                                        minLines = 8,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                    )
                                }
                            }

                            if (model.ageVerified.value) {
                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(text = stringResource(R.string.profile_edit_dialog_title_age_verification_visibility), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                                        }
                                    )

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ComboInput(
                                            options = listOf("hidden", "verified", "18+"),
                                            readableOptions = mapOf("hidden" to "Hidden", "verified" to "Verified", "18+" to "18+ Verified"),
                                            selection = model.verifiedStatus
                                        )
                                    }
                                }
                            }

                            item {
                                ListItem(
                                    headlineContent = {
                                        Text(text = stringResource(R.string.profile_edit_dialog_title_bio_links), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                                    }
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp),
                                    value = model.bioLinks[0],
                                    onValueChange = {
                                        model.bioLinks[0] = it
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                                    value = model.bioLinks[1],
                                    onValueChange = {
                                        model.bioLinks[1] = it
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                                    value = model.bioLinks[2],
                                    onValueChange = {
                                        model.bioLinks[2] = it
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                )
                            }
                        }
                    }
                }

                if (showSettingsSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showSettingsSheet = false
                            model.applySettings(true)
                        }, sheetState = settingsSheetState
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
                                            settingsSheetState.hide()
                                        }.invokeOnCompletion {
                                            if (!settingsSheetState.isVisible) {
                                                showSettingsSheet = false
                                            }
                                        }
                                    }) {
                                        Text(stringResource(R.string.search_filter_button_apply))
                                    }
                                }, headlineContent = { })
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_worlds)) },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Outlined.Cabin,
                                            contentDescription = null
                                        )
                                    })
                                HorizontalDivider(
                                    color = Color.Gray, thickness = 0.5.dp
                                )
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_worlds_featured)) },
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
                                    })
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_worlds_sort_by)) },
                                    trailingContent = {
                                        val options = listOf(
                                            "popularity",
                                            "heat",
                                            "trust",
                                            "shuffle",
                                            "random",
                                            "favorites",
                                            "publicationDate",
                                            "labsPublicationDate",
                                            "created",
                                            "updated",
                                            "order",
                                            "relevance",
                                            "name"
                                        )
                                        ComboInput(
                                            options = options, selection = model.sortWorlds
                                        )
                                    })
                            }
                            item {
                                var worldCount by remember { mutableStateOf(model.worldsAmount.intValue.toString()) }
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_worlds_count)) },
                                    trailingContent = {
                                        OutlinedTextField(
                                            value = worldCount,
                                            onValueChange = {
                                                worldCount = it
                                                if (it.isNotEmpty()) model.worldsAmount.intValue =
                                                    it.toIntOrNull() ?: model.worldsAmount.intValue
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    })
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_users)) },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Outlined.People,
                                            contentDescription = null
                                        )
                                    })
                                HorizontalDivider(
                                    color = Color.Gray, thickness = 0.5.dp
                                )
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_users_count)) },
                                    trailingContent = {
                                        OutlinedTextField(
                                            value = model.usersAmount.intValue.toString(),
                                            onValueChange = {
                                                if (it.isNotEmpty()) model.usersAmount.intValue =
                                                    it.toInt()
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    })
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_avatars)) },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Outlined.Person,
                                            contentDescription = null
                                        )
                                    })
                                HorizontalDivider(
                                    color = Color.Gray, thickness = 0.5.dp
                                )
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_avatars_provider)) },
                                    trailingContent = {
                                        val options = listOf("avtrdb", "justhparty")
                                        val optionsReadable = mapOf(
                                            "avtrdb" to "avtrDB", "justhparty" to "Just-H Party"
                                        )
                                        ComboInput(
                                            options = options,
                                            selection = model.avatarProvider,
                                            readableOptions = optionsReadable
                                        )
                                    })
                            }
                            item {
                                var avatarCount by remember { mutableStateOf(model.avatarsAmount.intValue.toString()) }
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_label_count)) },
                                    trailingContent = {
                                        OutlinedTextField(
                                            value = avatarCount,
                                            onValueChange = {
                                                avatarCount = it
                                                if (it.isNotEmpty()) model.avatarsAmount.intValue =
                                                    it.toIntOrNull() ?: model.avatarsAmount.intValue
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    })
                            }
                            item {
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_groups)) },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Outlined.Groups,
                                            contentDescription = null
                                        )
                                    })
                                HorizontalDivider(
                                    color = Color.Gray, thickness = 0.5.dp
                                )
                            }
                            item {
                                var groupCount by remember { mutableStateOf(model.groupsAmount.intValue.toString()) }
                                ListItem(headlineContent = { Text(stringResource(R.string.search_filter_category_groups_count)) },
                                    trailingContent = {
                                        OutlinedTextField(
                                            value = groupCount,
                                            onValueChange = {
                                                groupCount = it
                                                if (it.isNotEmpty()) model.groupsAmount.intValue =
                                                    it.toIntOrNull() ?: model.groupsAmount.intValue
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    })
                            }
                        }
                    }
                }
            }, bottomBar = {
                if (!model.searchModeActivated.value) {
                    NavigationBar {
                        tabs.forEach { tab ->
                            NavigationBarItem(selected = tabNavigator.current.key == tab.key,
                                onClick = {
                                    pressBackCounter = 0
                                    tabNavigator.current = tab
                                },
                                icon = {
                                    Icon(
                                        painter = tab.options.icon!!,
                                        contentDescription = tab.options.title
                                    )
                                },
                                label = {
                                    if (!App.isMinimalistModeEnabled()) {
                                        Text(text = tab.options.title)
                                    }
                                })
                        }
                    }
                }
            })
        }
    }
}