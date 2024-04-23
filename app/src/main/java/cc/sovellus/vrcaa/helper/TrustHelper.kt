package cc.sovellus.vrcaa.helper

import androidx.compose.ui.graphics.Color

object TrustHelper {

    enum class Rank {
        Troll,
        Visitor,
        NewUser,
        User,
        Known,
        Trusted,
        Administrator;

        override fun toString(): String {
            return when (this) {
                Administrator -> "Administrator"
                Trusted -> "Trusted"
                Known -> "Known"
                User -> "User"
                NewUser -> "New User"
                Visitor -> "Visitor"
                Troll -> "Troll"
            }
        }

        fun toColor(): Color {
            return when (this) {
                Administrator -> Color(181, 38, 38)
                Trusted -> Color(129, 67, 230)
                Known -> Color(255, 123, 66)
                User -> Color(43, 207, 92)
                NewUser -> Color(23, 120, 255)
                Visitor -> Color(204, 204, 204)
                Troll -> Color(204, 204, 204)
            }
        }
    }

    fun getTrustRankFromTags(tags: List<String>): Rank {
        var rank: Rank = Rank.Troll

        for (tag in tags.reversed()) {

            if (tag.contains("admin_moderator") && (rank < Rank.Administrator))
                rank = Rank.Administrator

            if (tag.contains("system_trust_veteran") && (rank < Rank.Trusted))
                rank = Rank.Trusted

            if (tag.contains("system_trust_trusted") && (rank < Rank.Known))
                rank = Rank.Known

            if (tag.contains("system_trust_known") && (rank < Rank.User))
                rank = Rank.User

            if (tag.contains("system_trust_basic") && (rank < Rank.NewUser))
                rank = Rank.NewUser

            if (!tag.contains("system_troll") && (rank < Rank.Visitor))
                rank = Rank.Visitor
        }

        return rank
    }
}