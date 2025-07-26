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


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen

class PeekActivity : BaseActivity() {

    lateinit var type: String
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var path = intent.data?.path
        path = path?.substring(6)

        val data = path?.split("/")

        data?.let {
            if (it.size >= 2) {
                type = data[0]
                id = data[1]
            } else {
                type = "unknown"
                id = "0"
            }
        }
    }

    @Composable
    override fun Content(bundle: Bundle?) {
        var screen: Screen? = null

        when (type) {
            "world" -> {
                screen = WorldScreen(id, true)
            }
            "user" -> {
                screen = UserProfileScreen(id, true)
            }
            "avatar" -> {
                screen = AvatarScreen(id, true)
            }
            "group" -> {
                screen = GroupScreen(id, true)
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
    }
}