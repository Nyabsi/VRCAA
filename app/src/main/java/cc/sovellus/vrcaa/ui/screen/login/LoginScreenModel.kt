package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.VRChatApi
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.extension.userCredentials
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.ApiManager.cache
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val context: Context,
    private val navigator: Navigator
) : ScreenModel {

    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

    var username = mutableStateOf(preferences.userCredentials.first.let { it ?: "" })
    var password = mutableStateOf(preferences.userCredentials.second.let { it ?: "" })

    fun doLogin() {
        screenModelScope.launch {
            api.getToken(username.value, password.value, preferences.twoFactorToken).let { result ->
                if (result == null) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_toast_wrong_credentials),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (result.mfaType == VRChatApi.MfaType.NONE)
                    {
                        cache.forceCacheRefresh()
                        preferences.authToken = result.token
                        navigator.replace(NavigationScreen())
                    } else {
                        preferences.userCredentials = Pair(username.value, password.value)
                        navigator.replace(MfaScreen(result.mfaType))
                    }
                }
            }
        }
    }
}