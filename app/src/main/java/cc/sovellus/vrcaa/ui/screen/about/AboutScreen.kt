package cc.sovellus.vrcaa.ui.screen.about

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.ButtonItem
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.DividerH
import cc.sovellus.vrcaa.ui.components.base.ComposableBase.Companion.TextWithHeaderAndDescription
import cc.sovellus.vrcaa.ui.screen.licenses.LicensesScreen

class AboutScreen : Screen {

    override val key = uniqueScreenKey

    @SuppressLint("BatteryLife")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

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

                    title = { Text(text = stringResource(R.string.DeleteMe)) }
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        ),
                ) {
                    item {
                        Spacer(modifier = Modifier.padding(8.dp))

                        Box(
                            modifier = Modifier
                                .height(128.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Image(
                                painter = if (App.isAppInDarkTheme()) { painterResource(R.drawable.logo_dark) } else { painterResource(R.drawable.logo_white) },
                                contentDescription = null,
                                contentScale = ContentScale.FillHeight,
                                alignment = Alignment.Center
                            )
                        }

                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                    TextWithHeaderAndDescription("Version", "${BuildConfig.VERSION_NAME} ${BuildConfig.FLAVOR} (${BuildConfig.GIT_BRANCH}, ${BuildConfig.GIT_HASH})")
                    TextWithHeaderAndDescription("Model", Build.MODEL)
                    TextWithHeaderAndDescription("Vendor", Build.MANUFACTURER)
                    TextWithHeaderAndDescription("System Version", "Android ${Build.VERSION.RELEASE}", true)
                    DividerH()
                    ButtonItem(R.string.about_page_open_source_licenses_title, 0) {navigator.push(LicensesScreen())}
                }
            }
        )
    }
}