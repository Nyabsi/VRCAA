package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.api.ApiContext
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val api: ApiContext,
    private val context: Context,
    private val navigator: Navigator
) : ScreenModel {

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var passwordVisible = mutableStateOf(false)

    fun doLogin() {
        screenModelScope.launch {
            val token = api.getToken(username.value, password.value)
            if (token.isNotEmpty()) {
                navigator.popAll()
                navigator.push(TwoAuthScreen(token))
            } else {
                Toast.makeText(
                    context,
                    "Wrong username or password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}