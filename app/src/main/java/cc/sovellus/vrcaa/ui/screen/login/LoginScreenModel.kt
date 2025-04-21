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
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    callback(false, IAuth.AuthType.AUTH_NONE)
                }
            }
        }
    }
}