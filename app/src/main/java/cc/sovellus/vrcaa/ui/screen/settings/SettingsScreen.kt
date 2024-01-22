package cc.sovellus.vrcaa.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen

class SettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val screenModel = navigator.rememberNavigatorScreenModel { SettingsScreenModel(ApiContext(context), context) }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
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
                        .padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        ListItem(
                            headlineContent = { Text("Open Source Licenses") },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Dehaze,
                                    contentDescription = "Localized description"
                                )
                            },
                            modifier = Modifier.clickable(
                                onClick = {
                                    navigator.push(LicensesScreen())
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
                            screenModel.doLogout()
                            navigator.popAll()
                            navigator.push(LoginScreen())
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(text = "Logout")
                    }
                }
            }
        )
    }
}