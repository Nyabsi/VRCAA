package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IAuth
import cc.sovellus.vrcaa.extension.userCredentials
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class LoginScreenModel : ScreenModel {

    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

    var username = mutableStateOf(preferences.userCredentials.first.let { it ?: "" })
    var password = mutableStateOf(preferences.userCredentials.second.let { it ?: "" })

    fun doLogin(callback: (success: Boolean, type: IAuth.AuthType) -> Unit) {
        screenModelScope.launch {
            api.auth.login(username.value, password.value).let { result ->
                if (result.success) {
                    if (result.authType == IAuth.AuthType.AUTH_NONE) {
                        val intent = Intent(context, PipelineService::class.java)
                        context.startService(intent)
                    }
                    callback(true, result.authType)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_toast_wrong_credentials),
                        Toast.LENGTH_SHORT
                    ).show()
                    callback(false, IAuth.AuthType.AUTH_NONE)
                }
            }
        }
    }
}