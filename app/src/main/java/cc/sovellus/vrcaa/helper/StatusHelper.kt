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
                Busy -> Color(91, 11, 11)
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