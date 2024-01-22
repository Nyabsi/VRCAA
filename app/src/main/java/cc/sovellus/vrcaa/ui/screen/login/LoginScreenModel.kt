package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.helper.userCredentials
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
                Log.d("VRCAA", "WAT THE FUCK!?!?!")
                val preferences = context.getSharedPreferences(
                    "vrcaa_prefs", MODE_PRIVATE
                )

                // STORE credentials, so we can request new session later when it expires, for any given reason.
                preferences.userCredentials = Pair(username.value, password.value)
                preferences.cookies = token

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