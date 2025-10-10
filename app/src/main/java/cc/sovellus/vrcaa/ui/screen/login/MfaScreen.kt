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

import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IAuth
import cc.sovellus.vrcaa.ui.components.input.CodeInput
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen

class MfaScreen(
    private val authType: IAuth.AuthType
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {


        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val screenModel =
            navigator.rememberNavigatorScreenModel { MfaScreenModel(authType) }

        Scaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = padding.calculateTopPadding()),
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(Dp.Unspecified, 520.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = if (authType == IAuth.AuthType.AUTH_EMAIL) {
                            stringResource(R.string.auth_text_email)
                        } else {
                            stringResource(R.string.auth_text_app)
                        }
                    )

                    CodeInput(
                        input = screenModel.code
                    )

                    Button(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(4.dp), onClick = {
                            screenModel.verify { result ->
                                if (result) {
                                    navigator.replace(NavigationScreen())
                                }
                            }
                        }) {
                        Text(text = stringResource(R.string.auth_button_text))
                    }

                    Button(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(4.dp), onClick = {
                            val clipboard: ClipboardManager? =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            if (clipboard?.hasPrimaryClip() == true) {
                                val clipData = clipboard.primaryClip
                                if ((clipData?.itemCount ?: 0) > 0) {
                                    val clipItem = clipData?.getItemAt(0)
                                    val clipText = clipItem?.text?.toString()
                                    if (clipText?.length == 6) {
                                        screenModel.code.value = clipText
                                        screenModel.verify { result ->
                                            if (result) {
                                                navigator.replace(NavigationScreen())
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                        Text(text = stringResource(R.string.auth_button_paste))
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = padding.calculateBottomPadding()
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