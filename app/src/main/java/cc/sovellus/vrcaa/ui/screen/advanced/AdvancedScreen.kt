package cc.sovellus.vrcaa.ui.screen.advanced

import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.WifiFind
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.networkLogging
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ButtonItemWithIcon
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ContentHeader
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.SwitchItemWithIcon
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.QuickToast
import cc.sovellus.vrcaa.ui.screen.debug.DebugScreen

class AdvancedScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

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
                    ContentHeader(R.string.settings_Networking)
                    SwitchItemWithIcon(R.string.advanced_page_network_logging, R.string.advanced_page_network_logging_description, model.networkLoggingMode, Icons.Outlined.WifiFind) { toggled ->
                        model.preferences.networkLogging = toggled
                        QuickToast(context,R.string.developer_mode_toggle_toast)
                    }
                    ButtonItemWithIcon(R.string.advanced_page_view_network_logs, R.string.advanced_page_view_network_logs_description, Icons.Outlined.Insights) {
                        navigator.push(DebugScreen())
                    }

                    ContentHeader(R.string.advanced_page_background_activities)
                    ButtonItemWithIcon(R.string.advanced_page_battery_optimization, R.string.advanced_page_battery_optimization_description, Icons.Outlined.BatteryStd) {
                        val manager = getSystemService(context, PowerManager::class.java)
                        manager?.let { pm ->
                            if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
                                val intent = Intent(
                                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            } else {
                                QuickToast(context, R.string.about_page_battery_optimizations_toast)
                            }
                        }
                    }
                }
            },
        )
    }
}