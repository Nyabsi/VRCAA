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

package cc.sovellus.vrcaa.widgets

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class FriendWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FriendWidget()
    private val coroutineScope = MainScope()

    val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: List<Friend>) {
            val intent = Intent(App.getContext(), FriendWidgetReceiver::class.java).apply {
                action = "FRIEND_LOCATION_UPDATE"
            }
            App.getContext().sendBroadcast(intent)
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        FriendManager.addListener(listener)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "FRIEND_LOCATION_UPDATE") {
            this.update(context)
        }
        super.onReceive(context, intent)
    }

    private fun update(context: Context) {
        coroutineScope.launch {
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(FriendWidget::class.java)
            glanceIds.forEach { glance ->
                glanceAppWidget.update(context, glance)
            }
        }
    }
}