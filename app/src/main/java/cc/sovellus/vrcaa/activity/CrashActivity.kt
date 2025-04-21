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

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import cc.sovellus.vrcaa.GlobalExceptionHandler
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.ui.screen.crash.CrashScreen

class CrashActivity : BaseActivity() {

    private lateinit var exception: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        exception = GlobalExceptionHandler.getThrowableFromIntent(intent)
    }

    @Composable
    override fun Content(bundle: Bundle?) {
        CrashScreen(
            exception = exception,
            onRestart = {
                finishAffinity()
                startActivity(Intent(this@CrashActivity, MainActivity::class.java))
            }
        )
    }
}