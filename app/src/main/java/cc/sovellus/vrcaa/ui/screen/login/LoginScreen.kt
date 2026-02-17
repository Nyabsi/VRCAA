/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IAuth
import cc.sovellus.vrcaa.ui.components.input.PasswordInput
import cc.sovellus.vrcaa.ui.components.input.TextInput
import cc.sovellus.vrcaa.ui.components.misc.Logo
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen

class LoginScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val screenModel = LoginScreenModel()

        var passwordVisibility by remember { mutableStateOf(false) }

        Scaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = padding.calculateTopPadding()),
                ) {
                Column(
                    modifier = Modifier
                        .widthIn(0.dp, 520.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Logo(size = 172.dp)

                    Text(text = stringResource(R.string.login_text))

                    TextInput(
                        title = stringResource(R.string.login_label_username),
                        input = screenModel.username
                    )

                    PasswordInput(
                        title = stringResource(R.string.login_label_password),
                        input = screenModel.password,
                        visible = passwordVisibility,
                        onVisibilityChange = {
                            passwordVisibility = !passwordVisibility
                        })

                    Button(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(8.dp), onClick = {
                            screenModel.doLogin { result, type ->
                                if (result) {
                                    if (type == IAuth.AuthType.AUTH_NONE)
                                        navigator.replace(NavigationScreen())
                                    else
                                        navigator.replace(MfaScreen(type))
                                }
                            }
                        }) {
                        Text(text = stringResource(R.string.login_button_text))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = padding.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        ),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.legal_disclaimer),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}