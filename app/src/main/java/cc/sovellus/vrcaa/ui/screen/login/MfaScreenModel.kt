package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IAuth
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class MfaScreenModel(
    private val authType: IAuth.AuthType
) : ScreenModel {

    private val context: Context = App.getContext()
    var code: MutableState<String> = mutableStateOf("")

    fun verify(callback: (success: Boolean) -> Unit) {
        screenModelScope.launch {
            api.auth.verify(authType, code.value).let { result ->
                if (result.success) {
                    val intent = Intent(context, PipelineService::class.java)
                    context.startService(intent)
                    callback(true)
                } else {
                    code.value = ""
                    Toast.makeText(
                        context, context.getString(R.string.login_toast_wrong_code), Toast.LENGTH_SHORT
                    ).show()
                    callback(false)
                }
            }
        }
    }
}