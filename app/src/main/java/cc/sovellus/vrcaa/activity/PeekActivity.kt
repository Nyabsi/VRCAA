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

package cc.sovellus.vrcaa.activity


import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen


class PeekActivity : BaseActivity() {

    var type: MutableState<String> = mutableStateOf("")
    var id: MutableState<String> = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var path = intent.data?.path
        path = path?.substring(6)

        val data = path?.split("/")

        val allowedPaths = listOf("world", "user", "avatar", "group")
        var earlyFinish = false

        try {
            if (!data.isNullOrEmpty() && allowedPaths.contains(data[0])) {
                type.value = data[0]
                id.value = data[1]
            } else {
                earlyFinish = true
            }
        } catch (_: Throwable) {
            earlyFinish = true
        }

        if (earlyFinish) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, "http://www.vrchat.com/$path".toUri())
                startActivity(intent)
                finish()
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    this.getString(R.string.activity_peek_no_url_handler),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    @Composable
    override fun Content(bundle: Bundle?) {
        var screen: Screen? = null

        if (type.value.isNotEmpty() && id.value.isNotEmpty()) {
            when (type.value) {
                "world" -> {
                    screen = WorldScreen(id.value, true)
                }
                "user" -> {
                    screen = UserProfileScreen(id.value, true)
                }
                "avatar" -> {
                    screen = AvatarScreen(id.value, true)
                }
                "group" -> {
                    screen = GroupScreen(id.value, true)
                }
                else -> finish()
            }

            screen?.let {
                Navigator(
                    screen = it,
                    disposeBehavior = NavigatorDisposeBehavior(
                        disposeNestedNavigators = false,
                        disposeSteps = false
                    ),
                    onBackPressed = { true }
                ) { navigator ->
                    SlideTransition(navigator = navigator)
                }
            }
        } else {
            finish()
        }
    }
}