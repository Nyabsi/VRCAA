package cc.sovellus.vrcaa.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen

class SettingsScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { SettingsScreenModel(context) }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },

                    title = { Text(text = "Settings") }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding()),
                ) {
                    item {
                        ListItem(
                            headlineContent = { Text("About") },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Dehaze,
                                    contentDescription = "Localized description"
                                )
                            },
                            modifier = Modifier.clickable(
                                onClick = {
                                    navigator.push(AboutScreen())
                                }
                            )
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            model.doLogout()
                            navigator.popUntilRoot()
                            navigator.replace(LoginScreen())
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.logout_button_text))
                    }
                }
            }
        )
    }
}