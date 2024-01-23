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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.api.ApiContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TwoAuthScreen(
   private val token: String
) : Screen {

    @Composable
    override fun Content() {


        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val screenModel = navigator.rememberNavigatorScreenModel { TwoAuthScreenModel(
            ApiContext(context),
            context
        ) }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Input the code sent to your e-mail")

            TextInput(
                title = "2FA Code",
                input = screenModel.code
            )

            Button(
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                onClick = {
                    screenModel.doVerify(token, navigator)
                }
            ) {
                Text(text = "Verify")
            }
        }

        Column (
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VRCAA is not endorced by VRChat Inc. or any of their affiliates.",
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 12.sp
            )
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun TextInput(
        title: String,
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
                keyboardType = KeyboardType.NumberPassword
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
                                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
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