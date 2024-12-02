package cc.sovellus.vrcaa.ui.screen.theme

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.minimalistMode
import cc.sovellus.vrcaa.manager.ThemeManager
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ButtonItem
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ButtonItemWithIcon
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ContentHeader
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.DividerH
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.QuickToast
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.SegmentedRowItem
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.SwitchItem
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.SwitchItemWithIcon
import cc.sovellus.vrcaa.ui.screen.navigationButtons.NavigationButtonSettingsScreen


class ThemeScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val model = navigator.rememberNavigatorScreenModel { ThemeScreenModel() }


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

                    title = { Text(text = stringResource(R.string.theme_page_title)) }
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

                    ContentHeader(R.string.theme_page_section_theme_title)

                    SegmentedRowItem(R.array.theme_page_selection_options, model.currentIndex) {changed ->
                        model.preferences.currentThemeOption = changed
                        ThemeManager.setTheme(model.currentIndex.intValue)
                    }

                    ContentHeader(R.string.theme_page_section_display_title)

                    SwitchItemWithIcon(
                        R.string.theme_page_minimalist_mode_text, R.string.theme_page_minimalist_mode_text_description, model.minimalistMode, Icons.Outlined.Screenshot) { toggled ->
                        model.preferences.minimalistMode = toggled
                        QuickToast(context, R.string.developer_mode_toggle_toast)
                    }

                    ButtonItemWithIcon(R.string.settings_navBar, R.string.settings_navBar_description, Icons.Outlined.EditNote) {
                        navigator.push(NavigationButtonSettingsScreen())
                    }
                    DividerH()
                }
            },
        )
    }
}