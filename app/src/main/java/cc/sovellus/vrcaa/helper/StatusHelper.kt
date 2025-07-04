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

package cc.sovellus.vrcaa.helper

import androidx.compose.ui.graphics.Color

object StatusHelper {
    enum class Status {
        JoinMe,
        Active,
        AskMe,
        Busy,
        Offline;

        fun toColor(): Color {
            return when (this) {
                JoinMe -> Color(66, 201, 255)
                Active -> Color(81, 229, 125)
                AskMe -> Color(232, 130, 52)
                Busy -> Color(120, 11, 11)
                else -> Color.Gray
            }
        }

        override fun toString(): String {
            return when (this) {
                JoinMe -> "Join Me"
                Active -> "Online"
                AskMe -> "Ask Me"
                Busy -> "Busy"
                else -> "Offline"
            }
        }

        companion object {
            fun fromInt(value: Int) = Status.entries.first { it.ordinal == value }
        }
    }

    fun getStatusFromString(status: String): Status {
        return when (status) {
            "join me" -> Status.JoinMe
            "active" -> Status.Active
            "ask me" -> Status.AskMe
            "busy" -> Status.Busy
            else -> Status.Offline
        }
    }
}