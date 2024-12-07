package cc.sovellus.vrcaa.ui.screen.advanced

import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getSystemService
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.networkLogging
import cc.sovellus.vrcaa.ui.components.base.ContentHeader
import cc.sovellus.vrcaa.ui.components.base.buttonWithIcon
import cc.sovellus.vrcaa.ui.components.base.quickToast
import cc.sovellus.vrcaa.ui.components.base.toggleWithIcon
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
                    toggleWithIcon(
                        R.string.advanced_page_network_logging,
                        R.string.advanced_page_network_logging_description,
                        model.networkLoggingMode,
                        Icons.Outlined.WifiFind
                    ) { toggled ->
                        model.preferences.networkLogging = toggled
                        quickToast(context, R.string.developer_mode_toggle_toast)
                    }
                    buttonWithIcon(
                        R.string.advanced_page_view_network_logs,
                        R.string.advanced_page_view_network_logs_description,
                        Icons.Outlined.Insights,
                        true
                    ) {
                        navigator.push(DebugScreen())
                    }

                    ContentHeader(R.string.advanced_page_background_activities)
                    buttonWithIcon(
                        R.string.advanced_page_battery_optimization,
                        R.string.advanced_page_battery_optimization_description,
                        Icons.Outlined.BatteryStd
                    ) {
                        val manager = getSystemService(context, PowerManager::class.java)
                        manager?.let { pm ->
                            if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
                                val intent = Intent(
                                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            } else {
                                quickToast(context, R.string.about_page_battery_optimizations_toast)
                            }
                        }
                    }
                }
            },
        )
    }
}