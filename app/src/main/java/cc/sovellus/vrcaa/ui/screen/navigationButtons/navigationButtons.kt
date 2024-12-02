package cc.sovellus.vrcaa.ui.screen.navigationButtons

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.FeedTab
import cc.sovellus.vrcaa.extension.favoritesTab
import cc.sovellus.vrcaa.extension.friendsTab
import cc.sovellus.vrcaa.extension.homeTab
import cc.sovellus.vrcaa.extension.searchFeaturedWorlds
import cc.sovellus.vrcaa.extension.settingsTab
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ContentHeader
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.SegmentedRowItem

class NavigationButtonSettingsScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val model = navigator.rememberNavigatorScreenModel { NavigationButtonSettingsModel() }

        val options =
            stringArrayResource(R.array.navigation_buttons_settings_page_selection_options)


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

                    title = { Text(text = stringResource(R.string.navButtons_page_title)) }
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
                    ContentHeader(R.string.tabs_label_home)
                    SegmentedRowItem(R.array.navigation_buttons_settings_page_selection_options, model.currentIndexHo) { changed ->
                        model.preferences.homeTab = changed
                    }

                    ContentHeader(R.string.tabs_label_friends)
                    SegmentedRowItem(R.array.navigation_buttons_settings_page_selection_options, model.currentIndexFr) { changed ->
                        model.preferences.friendsTab = changed
                    }

                    ContentHeader(R.string.tabs_label_favorites)
                    SegmentedRowItem(R.array.navigation_buttons_settings_page_selection_options, model.currentIndexFa) { changed ->
                        model.preferences.favoritesTab = changed
                    }

                    ContentHeader(R.string.tabs_label_feed)
                    SegmentedRowItem(R.array.navigation_buttons_settings_page_selection_options, model.currentIndexFe) { changed ->
                        model.preferences.FeedTab = changed
                    }

                }
            },
        )
    }
}