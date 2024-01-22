package cc.sovellus.vrcaa.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.api.ApiContext

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
            modifier = Modifier.fillMaxSize(),
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VRCAA is not endorced by VRChat Inc. or any of their affiliates."
            )
        }
    }

    @Composable
    fun TextInput(
        title: String,
        input: MutableState<String>
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = input.value,
            onValueChange = { input.value = it },
            label = { Text(text = title) },
            singleLine = true,
        )
    }
}