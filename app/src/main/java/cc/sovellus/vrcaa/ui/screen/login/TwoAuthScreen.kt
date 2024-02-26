package cc.sovellus.vrcaa.ui.screen.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.ui.theme.isSystemDarkMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TwoAuthScreen(
    private val otpType: ApiContext.TwoFactorType,
    private val token: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {


        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val screenModel = navigator.rememberNavigatorScreenModel { TwoAuthScreenModel(context) }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (otpType == ApiContext.TwoFactorType.EMAIL_OTP) {
                    stringResource(R.string.auth_text_email)
                } else {
                    stringResource(R.string.auth_text_app)
                }
            )

            TextInput(
                input = screenModel.code
            )

            Button(
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                onClick = {
                    screenModel.doVerify(otpType, token, navigator)
                }
            ) {
                Text(text = stringResource(R.string.auth_button_text))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
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

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun TextInput(
        input: MutableState<String>
    ) {
        val focus = LocalFocusManager.current
        val coroutineScope = rememberCoroutineScope()

        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .onFocusChanged {
                    coroutineScope.launch {
                        delay(5000)
                        focus.clearFocus()
                    }
                },
            value = input.value,
            onValueChange = {
                if (it.length <= 6)
                    input.value = it
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii
            ),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement
                        .spacedBy(
                            space = 6.dp,
                            alignment = Alignment.CenterHorizontally
                        )
                ) {
                    repeat(6) {
                        val char = when {
                            it >= input.value.length -> ""
                            else -> input.value[it].toString()
                        }

                        Text(
                            modifier = Modifier
                                .width(50.dp)
                                .height(60.dp)
                                .border(
                                    1.dp, if (isSystemDarkMode()) {
                                        Color.White
                                    } else {
                                        Color.Black
                                    }, RoundedCornerShape(8.dp)
                                )
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(top = 10.dp),
                            text = char,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }
        )
    }
}